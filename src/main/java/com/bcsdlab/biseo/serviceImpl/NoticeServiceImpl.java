package com.bcsdlab.biseo.serviceImpl;

import com.bcsdlab.biseo.dto.notice.model.NoticeAndFileModel;
import com.bcsdlab.biseo.dto.notice.model.NoticeFileModel;
import com.bcsdlab.biseo.dto.notice.model.NoticeModel;
import com.bcsdlab.biseo.dto.notice.model.NoticeReadModel;
import com.bcsdlab.biseo.dto.notice.request.NoticeRequestDTO;
import com.bcsdlab.biseo.dto.notice.response.FileInfoDto;
import com.bcsdlab.biseo.dto.notice.response.NoticeListResponseDTO;
import com.bcsdlab.biseo.dto.notice.response.NoticeResponseDTO;
import com.bcsdlab.biseo.dto.notice.model.NoticeTargetModel;
import com.bcsdlab.biseo.dto.user.model.UserModel;
import com.bcsdlab.biseo.dto.user.response.UserResponseDTO;
import com.bcsdlab.biseo.enums.Department;
import com.bcsdlab.biseo.enums.ErrorMessage;
import com.bcsdlab.biseo.enums.FileType;
import com.bcsdlab.biseo.exception.AuthException;
import com.bcsdlab.biseo.exception.BadRequestException;
import com.bcsdlab.biseo.exception.CriticalException;
import com.bcsdlab.biseo.exception.NotFoundException;
import com.bcsdlab.biseo.mapper.NoticeMapper;
import com.bcsdlab.biseo.mapper.UserMapper;
import com.bcsdlab.biseo.repository.NoticeRepository;
import com.bcsdlab.biseo.repository.UserRepository;
import com.bcsdlab.biseo.service.NoticeService;
import com.bcsdlab.biseo.util.JwtUtil;
import com.bcsdlab.biseo.util.S3Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
    private static String extRegExp = "^([\\S\\s]+(\\.(?i)(jpg|jpeg|png|gif|bmp))$)";
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final S3Util s3Util;

    @Override
    @Transactional
    public Long createNotice(NoticeRequestDTO request) {
        // 예외 처리
        if (request.getGrade().size() == 0) {
            throw new BadRequestException(ErrorMessage.GRADE_REQUIRED);
        }

        // notice 기본 정보 저장
        NoticeModel notice = NoticeMapper.INSTANCE.toNoticeModel(request);
        Long userId = Long.parseLong(jwtUtil.findUserInfoInToken().getAudience().get(0));
        notice.setUserId(userId);
        noticeRepository.createNotice(notice);

        // notice target 학과/학년 저장
        List<NoticeTargetModel> targetList = new ArrayList<>();
        // 학생회 : 0
        targetList.add(new NoticeTargetModel(notice.getId(), request.getDepartment().getValue()));
        for (Integer grade : request.getGrade()) {
            targetList.add(new NoticeTargetModel(notice.getId(), request.getDepartment().getValue() + grade));
        }
        noticeRepository.createTarget(targetList);

        // notice File 저장
        List<NoticeFileModel> fileList = uploadFiles(notice, request.getFiles());
        if (fileList != null) {
            noticeRepository.createFiles(fileList);
        }
        // notice 썸네일 저장
        noticeRepository.updateThumbnail(notice);

        // TODO : notice target에 푸시 알림

        return notice.getId();
    }

    @Override
    @Transactional
    public NoticeResponseDTO getNotice(Long noticeId) {
        if (noticeId < 1) {
            throw new BadRequestException(ErrorMessage.INVALID_NOTICE_ID);
        }
        Long userId = Long.parseLong(jwtUtil.findUserInfoInToken().getAudience().get(0));

        // 공지 조회
        NoticeAndFileModel noticeAndFile = noticeRepository.findNoticeAndFileById(noticeId, userId);
        if (noticeAndFile == null) {
            throw new NotFoundException(ErrorMessage.NOTICE_NOT_FOUND);
        }

        // 조회 가능한 학과인가?
        Integer userDepartment = userRepository.findUserDepartmentById(userId);
        List<Integer> noticeTarget = noticeRepository.findTargetByNoticeId(noticeId);
        if (!noticeTarget.contains(userDepartment)) {
            throw new AuthException(ErrorMessage.NO_AUTHORIZATION);
        }

        NoticeResponseDTO response = NoticeMapper.INSTANCE.toResponseDTO(noticeAndFile);

        // File, Img 구분
        for (NoticeFileModel file : noticeAndFile.getFiles()) {
            if (file.getType() == FileType.FILE) {
                response.getFiles().add(new FileInfoDto(file));
            } else if (file.getType() == FileType.IMG) {
                response.getImgs().add(new FileInfoDto(file));
            }
        }

        // 읽은 유저 읽음처리
        if (noticeRepository.findReadLogByUserId(noticeId, userId) == null) {
            NoticeReadModel noticeReadModel = NoticeReadModel.builder()
                .userId(userId)
                .noticeId(noticeId)
                .build();
            noticeRepository.createReadLog(noticeReadModel);
        }
        return response;
    }


    // 커서기반 페이지네이션
    @Override
    public List<NoticeListResponseDTO> getNoticeList(String searchBy, Long cursor, Integer limits) {
        Long userId = Long.parseLong(jwtUtil.findUserInfoInToken().getAudience().get(0));
        Integer userDepartment = userRepository.findUserDepartmentById(userId);

        return noticeRepository.getNoticeList(userDepartment, userId, searchBy, cursor, limits);
    }

    @Override
    @Transactional
    public List<UserResponseDTO> getReadLog(Long noticeId, Boolean isRead) {
        if (noticeId < 1 || isRead == null) {
            throw new BadRequestException(ErrorMessage.INVALID_NOTICE_ID);
        }
        NoticeModel notice = noticeRepository.findNoticeById(noticeId);
        if (notice == null) {
            throw new NotFoundException(ErrorMessage.NOTICE_NOT_FOUND);
        }
        List<UserModel> userList = isRead ? noticeRepository.findReadLogByNoticeId(noticeId)
            : noticeRepository.findNotReadLogByNoticeId(noticeId);

        List<UserResponseDTO> responses = new ArrayList<>();
        for (UserModel model : userList) {
            UserResponseDTO response = UserMapper.INSTANCE.toUserResponse(model);
            response.setGrade(model.getDepartment() % 10);
            response.setDepartment(Department.getDepartment(model.getDepartment() / 10 * 10));
            responses.add(response);
        }
        return responses;
    }

    @Override
    @Transactional
    public Long updateNotice(Long noticeId, NoticeRequestDTO request) {
        // 공지가 존재하지 않는다면
        NoticeModel notice = noticeRepository.findNoticeById(noticeId);
        if (notice == null) {
            throw new NotFoundException(ErrorMessage.NOTICE_NOT_FOUND);
        }

        // 작성자가 아니라면
        Long userId = Long.parseLong(jwtUtil.findUserInfoInToken().getAudience().get(0));
        if (!notice.getUserId().equals(userId)) {
            throw new AuthException(ErrorMessage.NO_AUTHORIZATION);
        }

        // 전부 다시 업로드
        // 게시글 : Update
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        noticeRepository.updateNoticeById(notice);

        // 타겟 : 삭제 후 재생성
        noticeRepository.deleteTargetByNoticeId(noticeId);
        List<NoticeTargetModel> targetList = new ArrayList<>();
        // 학생회 : 0
        targetList.add(new NoticeTargetModel(notice.getId(), request.getDepartment().getValue()));
        for (Integer grade : request.getGrade()) {
            targetList.add(new NoticeTargetModel(notice.getId(), request.getDepartment().getValue() + grade));
        }
        noticeRepository.createTarget(targetList);

        // 읽은 유저 : 전체 삭제
        noticeRepository.deleteReadListByNoticeId(noticeId);

        // notice File 저장
        noticeRepository.deleteNoticeFileByNoticeId(noticeId);
        List<NoticeFileModel> fileList = uploadFiles(notice, request.getFiles());
        if (fileList != null) {
            noticeRepository.createFiles(fileList);
        }
        // notice 썸네일 저장
        noticeRepository.updateThumbnail(notice);

        // TODO : notice target에 푸시 알림

        return notice.getId();
    }

    @Override
    @Transactional
    public String deleteNotice(Long noticeId) {
        NoticeModel notice = noticeRepository.findNoticeById(noticeId);
        if (notice == null) {
            throw new NotFoundException(ErrorMessage.NOTICE_NOT_FOUND);
        }

        // 작성자가 아니라면
        Long userId = Long.parseLong(jwtUtil.findUserInfoInToken().getAudience().get(0));
        if (!notice.getUserId().equals(userId)) {
            throw new AuthException(ErrorMessage.NO_AUTHORIZATION);
        }

        noticeRepository.deleteNoticeById(noticeId);
        noticeRepository.deleteTargetByNoticeId(noticeId);
        noticeRepository.deleteReadListByNoticeId(noticeId);
        noticeRepository.deleteNoticeFileByNoticeId(noticeId);

        return "게시글 삭제 완료";
    }

    private List<NoticeFileModel> uploadFiles(NoticeModel notice, List<MultipartFile> files) {
        List<NoticeFileModel> noticeFiles = new ArrayList<>();
        notice.setThumbnail(null);
        if (files.size() == 0) {
            return null;
        }

        for (MultipartFile file : files) {
            NoticeFileModel noticeFile = new NoticeFileModel();
            UUID uuid = UUID.randomUUID();
            String savedName = "files/" + notice.getId() + "/" + uuid + "/" + file.getOriginalFilename();
            noticeFile.setNoticeId(notice.getId());
            noticeFile.setSavedName(file.getOriginalFilename());
            noticeFile.setType(checkFileType(file.getOriginalFilename()));
            try {
                noticeFile.setPath(s3Util.uploadFile(savedName, file));
            } catch (IOException e) {
                throw new CriticalException(ErrorMessage.FILE_UPLOAD_FAIL);
            }
            noticeFiles.add(noticeFile);

            if (notice.getThumbnail() == null && noticeFile.getType() == FileType.IMG) {
                notice.setThumbnail(noticeFile.getPath());
            }
        }
        return noticeFiles;
    }

    private FileType checkFileType(String fileName) {
        if (fileName.matches(extRegExp)) {
            return FileType.IMG;
        }
        return FileType.FILE;
    }
}

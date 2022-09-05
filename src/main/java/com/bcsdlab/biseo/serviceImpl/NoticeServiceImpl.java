package com.bcsdlab.biseo.serviceImpl;

import com.bcsdlab.biseo.dto.notice.NoticeAndFileModel;
import com.bcsdlab.biseo.dto.notice.NoticeFileModel;
import com.bcsdlab.biseo.dto.notice.NoticeModel;
import com.bcsdlab.biseo.dto.notice.NoticeReadModel;
import com.bcsdlab.biseo.dto.notice.NoticeRequestDTO;
import com.bcsdlab.biseo.dto.notice.NoticeResponseDTO;
import com.bcsdlab.biseo.dto.notice.NoticeTargetModel;
import com.bcsdlab.biseo.dto.user.UserModel;
import com.bcsdlab.biseo.dto.user.UserResponseDTO;
import com.bcsdlab.biseo.enums.Department;
import com.bcsdlab.biseo.enums.FileType;
import com.bcsdlab.biseo.mapper.NoticeMapper;
import com.bcsdlab.biseo.mapper.UserMapper;
import com.bcsdlab.biseo.repository.NoticeRepository;
import com.bcsdlab.biseo.service.NoticeService;
import com.bcsdlab.biseo.util.JwtUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final JwtUtil jwtUtil;

    @Override
    public Long createNotice(NoticeRequestDTO request, List<MultipartFile> files) {
        // 예외 처리
        if (request.getGrade().size() == 0) {
            throw new RuntimeException("학년을 선택해야 합니다.");
        }

        // notice 기본 정보 저장
        NoticeModel notice = NoticeMapper.INSTANCE.toNoticeModel(request);
        Long userId = Long.parseLong(jwtUtil.findUserInfoInToken().getAudience().get(0));
        notice.setUserId(userId);
        noticeRepository.createNotice(notice);

        // notice target 학과/학년 저장
        List<Integer> targets = new ArrayList<>();
        for (Integer grade : request.getGrade()) {
            targets.add(request.getDepartment().getValue() + grade);
        }

        NoticeTargetModel targetModel = new NoticeTargetModel();
        targetModel.setNoticeId(notice.getId());
        for (Integer target : targets) {
            targetModel.setTarget(target);
            noticeRepository.createTarget(targetModel);
        }

        // notice File 저장
        // TODO : S3 파일저장 로직.

        // notice target에 푸시 알림

        return notice.getId();
    }

    @Override
    public NoticeResponseDTO getNotice(Long noticeId) {
        if (noticeId < 1) {
            throw new RuntimeException("잘못된 접근입니다.");
        }
        // 공지 조회
        NoticeAndFileModel noticeAndFile = noticeRepository.findByNoticeId(noticeId);
        if (noticeAndFile == null) {
            throw new RuntimeException("존재하지 않는 공지입니다.");
        }
        NoticeResponseDTO response = NoticeMapper.INSTANCE.toResponseDTO(noticeAndFile);

        // File, Img 구분
        for (NoticeFileModel file : noticeAndFile.getFiles()) {
            if (file.getType() == FileType.FILE) {
                response.getFiles().add(file.getPath());
            } else if (file.getType() == FileType.IMG) {
                response.getImgs().add(file.getPath());
            }
        }

        // 읽은 유저 읽음처리
        Long userId = Long.parseLong(jwtUtil.findUserInfoInToken().getAudience().get(0));
        if (noticeRepository.findReadLogByUserId(noticeId, userId) == null) {
            NoticeReadModel noticeReadModel = NoticeReadModel.builder()
                .userId(userId)
                .noticeId(noticeId)
                .build();
            noticeRepository.createReadLog(noticeReadModel);
        }
        return response;
    }

    @Override
    public List<UserResponseDTO> getReadLog(Long noticeId, Boolean isRead) {
        if (noticeId < 1 || isRead == null) {
            throw new RuntimeException("잘못된 접근입니다.");
        }
        List<UserModel> userList = isRead ? noticeRepository.findReadLogByNoticeId(noticeId)
            : noticeRepository.findNotReadLogByNoticeId(noticeId);
        if (userList == null) {
            throw new RuntimeException("존재하지 않는 포스트입니다.");
        }

        List<UserResponseDTO> responses = new ArrayList<>();
        for (UserModel model : userList) {
            UserResponseDTO response = UserMapper.INSTANCE.toUserResponse(model);
            response.setGrade(model.getDepartment() % 10);
            response.setDepartment(Department.getDepartment(model.getDepartment() / 10 * 10));
            responses.add(response);
        }
        return responses;
    }
}

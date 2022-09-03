package com.bcsdlab.biseo.serviceImpl;

import com.bcsdlab.biseo.dto.notice.NoticeModel;
import com.bcsdlab.biseo.dto.notice.NoticeRequestDTO;
import com.bcsdlab.biseo.dto.notice.NoticeResponseDTO;
import com.bcsdlab.biseo.dto.notice.NoticeTargetModel;
import com.bcsdlab.biseo.mapper.NoticeMapper;
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
        for(Integer grade : request.getGrade()) {
            targets.add(request.getDepartment().getValue() + grade);
        }

        NoticeTargetModel targetModel = new NoticeTargetModel();
        targetModel.setNoticeId(notice.getId());
        for(Integer target : targets) {
            targetModel.setTarget(target);
            noticeRepository.createTarget(targetModel);
        }

        // notice File 저장
        // TODO : S3 파일저장 로직.

        // notice target에 푸시 알림

        return notice.getId();
    }
}

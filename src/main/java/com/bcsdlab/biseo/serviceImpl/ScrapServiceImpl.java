package com.bcsdlab.biseo.serviceImpl;

import com.bcsdlab.biseo.dto.notice.model.NoticeAndFileModel;
import com.bcsdlab.biseo.dto.scrap.ScrapModel;
import com.bcsdlab.biseo.dto.scrap.response.ScrapResponse;
import com.bcsdlab.biseo.dto.user.model.UserModel;
import com.bcsdlab.biseo.mapper.ScrapMapper;
import com.bcsdlab.biseo.repository.NoticeRepository;
import com.bcsdlab.biseo.repository.ScrapRepository;
import com.bcsdlab.biseo.repository.UserRepository;
import com.bcsdlab.biseo.service.ScrapService;
import com.bcsdlab.biseo.util.JwtUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScrapServiceImpl implements ScrapService {

    private final UserRepository userRepository;
    private final ScrapRepository scrapRepository;
    private final NoticeRepository noticeRepository;
    private final JwtUtil jwtUtil;

    @Override
    public ScrapResponse createScrap(Long noticeId) {
        UserModel user = userRepository.findById(Long.parseLong(jwtUtil.findUserInfoInToken().getAudience().get(0)));
        if (user == null) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }
        List<Integer> noticeTarget = noticeRepository.findTargetByNoticeId(noticeId);
        if (noticeTarget.size() == 0) {
            throw new RuntimeException("공지가 존재하지 않습니다.");
        } else if (!noticeTarget.contains(user.getDepartment())) {
            throw new RuntimeException("스크랩 권한이 없습니다.");
        }

        ScrapModel scrap = scrapRepository.findScrapByUserIdAndNoticeId(user.getId(), noticeId);
        if (scrap != null) {
            throw new RuntimeException("스크랩이 이미 존재합니다.");
        }

        ScrapModel scrapModel = new ScrapModel();
        scrapModel.setUserId(user.getId());
        scrapModel.setNoticeId(noticeId);
        scrapRepository.createScrap(scrapModel);

        return ScrapMapper.INSTANCE.toScrapResponse(scrapModel);
    }

    @Override
    public void deleteScrap(Long scrapId) {
        UserModel user = userRepository.findById(Long.parseLong(jwtUtil.findUserInfoInToken().getAudience().get(0)));
        if (user == null) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }

        ScrapModel scrap = scrapRepository.findScrapById(scrapId);
        if (scrap == null) {
            throw new RuntimeException("스크랩이 존재하지 않습니다.");
        }

        if (!scrap.getUserId().equals(user.getId())) {
            throw new RuntimeException("스크랩 삭제 권한이 없습니다.");
        }

        scrapRepository.deleteScrapById(scrap.getId());
    }
}

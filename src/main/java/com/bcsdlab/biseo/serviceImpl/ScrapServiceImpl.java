package com.bcsdlab.biseo.serviceImpl;

import com.bcsdlab.biseo.dto.scrap.ScrapModel;
import com.bcsdlab.biseo.dto.scrap.response.ScrapListResponseDTO;
import com.bcsdlab.biseo.dto.scrap.response.ScrapResponseDTO;
import com.bcsdlab.biseo.dto.user.model.UserModel;
import com.bcsdlab.biseo.enums.ErrorMessage;
import com.bcsdlab.biseo.exception.AuthException;
import com.bcsdlab.biseo.exception.BadRequestException;
import com.bcsdlab.biseo.exception.NotFoundException;
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
    public ScrapResponseDTO createScrap(Long noticeId) {
        UserModel user = userRepository.findById(Long.parseLong(jwtUtil.findUserInfoInToken().getAudience().get(0)));
        if (user == null) {
            throw new BadRequestException(ErrorMessage.USER_NOT_EXIST);
        }
        List<Integer> noticeTarget = noticeRepository.findTargetByNoticeId(noticeId);
        if (noticeTarget.size() == 0) {
            throw new NotFoundException(ErrorMessage.NOTICE_NOT_FOUND);
        } else if (!noticeTarget.contains(user.getDepartment())) {
            throw new AuthException(ErrorMessage.SCRAP_DENIED);
        }

        ScrapModel scrap = scrapRepository.findScrapByUserIdAndNoticeId(user.getId(), noticeId);
        if (scrap != null) {
            throw new BadRequestException(ErrorMessage.SCRAP_EXIST);
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
            throw new BadRequestException(ErrorMessage.USER_NOT_EXIST);
        }

        ScrapModel scrap = scrapRepository.findScrapById(scrapId);
        if (scrap == null) {
            throw new BadRequestException(ErrorMessage.SCRAP_NOT_EXIST);
        }

        if (!scrap.getUserId().equals(user.getId())) {
            throw new AuthException(ErrorMessage.SCRAP_DELETE_DENIED);
        }

        scrapRepository.deleteScrapById(scrap.getId());
    }

    @Override
    public List<ScrapListResponseDTO> getScrapList(String searchBy, Long cursor, Integer limits) {
        Long userId = Long.parseLong(jwtUtil.findUserInfoInToken().getAudience().get(0));
        return scrapRepository.getScrapList(userId, searchBy, cursor, limits);
    }
}

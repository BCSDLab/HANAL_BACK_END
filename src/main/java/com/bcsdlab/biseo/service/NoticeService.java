package com.bcsdlab.biseo.service;

import com.bcsdlab.biseo.dto.notice.request.NoticeRequestDTO;
import com.bcsdlab.biseo.dto.notice.response.NoticeListDto;
import com.bcsdlab.biseo.dto.notice.response.NoticeListItemDTO;
import com.bcsdlab.biseo.dto.notice.response.NoticeResponseDTO;
import com.bcsdlab.biseo.dto.user.response.UserResponseDTO;
import java.util.List;

public interface NoticeService {

    Long createNotice(NoticeRequestDTO request);
    NoticeResponseDTO getNotice(Long noticeId);
    NoticeListDto getNoticeList(String searchBy, Long cursor, Integer limits);
    List<UserResponseDTO> getReadLog(Long noticeId, Boolean isRead);

    Long updateNotice(Long noticeId, NoticeRequestDTO request);

    String deleteNotice(Long noticeId);
}

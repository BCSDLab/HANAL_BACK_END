package com.bcsdlab.biseo.service;

import com.bcsdlab.biseo.dto.notice.NoticeRequestDTO;
import com.bcsdlab.biseo.dto.notice.NoticeResponseDTO;
import com.bcsdlab.biseo.dto.user.response.UserResponseDTO;
import java.util.List;

public interface NoticeService {

    Long createNotice(NoticeRequestDTO request);
    NoticeResponseDTO getNotice(Long noticeId);
    List<NoticeResponseDTO> getNoticeList(String searchBy, Long cursor, Integer limits);
    List<UserResponseDTO> getReadLog(Long noticeId, Boolean isRead);

    Long updateNotice(Long noticeId, NoticeRequestDTO request);

    String deleteNotice(Long noticeId);
}

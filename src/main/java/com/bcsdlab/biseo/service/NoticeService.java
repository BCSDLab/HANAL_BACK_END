package com.bcsdlab.biseo.service;

import com.bcsdlab.biseo.dto.notice.NoticeRequestDTO;
import com.bcsdlab.biseo.dto.notice.NoticeResponseDTO;
import com.bcsdlab.biseo.dto.user.UserResponseDTO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface NoticeService {

    Long createNotice(NoticeRequestDTO request, MultipartFile[] files);
    NoticeResponseDTO getNotice(Long noticeId);
    List<NoticeResponseDTO> getNoticeList(String searchBy, Long cursor, Integer limits);
    List<UserResponseDTO> getReadLog(Long noticeId, Boolean isRead);

    Long updateNotice(Long noticeId, NoticeRequestDTO request, MultipartFile[] files);

    String deleteNotice(Long noticeId);
}

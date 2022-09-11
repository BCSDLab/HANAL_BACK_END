package com.bcsdlab.biseo.service;

import com.bcsdlab.biseo.dto.notice.NoticeRequestDTO;
import com.bcsdlab.biseo.dto.notice.NoticeResponseDTO;
import com.bcsdlab.biseo.dto.user.UserResponseDTO;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface NoticeService {

    Long createNotice(NoticeRequestDTO request, List<MultipartFile> files);
    NoticeResponseDTO getNotice(Long noticeId);
    List<UserResponseDTO> getReadLog(Long noticeId, Boolean isRead);

    Long updateNotice(Long noticeId, NoticeRequestDTO request, List<MultipartFile> files);

    String deleteNotice(Long noticeId);
}

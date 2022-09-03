package com.bcsdlab.biseo.service;

import com.bcsdlab.biseo.dto.notice.NoticeRequestDTO;
import com.bcsdlab.biseo.dto.notice.NoticeResponseDTO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface NoticeService {

    Long createNotice(NoticeRequestDTO request, List<MultipartFile> files);
}

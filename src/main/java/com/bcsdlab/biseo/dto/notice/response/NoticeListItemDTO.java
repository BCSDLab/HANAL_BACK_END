package com.bcsdlab.biseo.dto.notice.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeListItemDTO {

    private Long id;
    private String userId;
    private String title;
    private String content;
    private String thumbnail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long scrapId;
}

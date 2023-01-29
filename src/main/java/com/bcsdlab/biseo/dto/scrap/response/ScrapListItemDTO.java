package com.bcsdlab.biseo.dto.scrap.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScrapListItemDTO {

    private Long Id;
    private String userId;
    private Long noticeId;
    private String title;
    private String content;
    private String thumbnail;
    private LocalDateTime createdAt;
}

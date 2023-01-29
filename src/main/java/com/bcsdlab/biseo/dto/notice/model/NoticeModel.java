package com.bcsdlab.biseo.dto.notice.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeModel {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String thumbnail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}

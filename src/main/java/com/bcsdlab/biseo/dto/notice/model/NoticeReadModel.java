package com.bcsdlab.biseo.dto.notice.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeReadModel {

    private Long id;
    private Long noticeId;
    private Long userId;
    private LocalDateTime readAt;
    private Boolean isDeleted;
}

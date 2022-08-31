package com.bcsdlab.biseo.dto.notice;

import java.sql.Timestamp;
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
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Boolean isDeleted;
}

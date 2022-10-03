package com.bcsdlab.biseo.dto.notice.model;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeTargetModel {
    private Long id;
    private Long noticeId;
    private int target;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Boolean isDeleted;

    public NoticeTargetModel(Long noticeId, int target) {
        this.noticeId = noticeId;
        this.target = target;
    }
}

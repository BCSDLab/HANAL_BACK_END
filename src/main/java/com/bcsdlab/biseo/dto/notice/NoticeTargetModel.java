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
public class NoticeTargetModel {
    private Long id;
    private Long noticeId;
    private int target;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}

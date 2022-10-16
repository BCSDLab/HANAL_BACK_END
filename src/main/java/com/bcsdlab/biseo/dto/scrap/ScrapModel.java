package com.bcsdlab.biseo.dto.scrap;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScrapModel {

    private Long id;
    private Long userId;
    private Long noticeId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Boolean isDeleted;
}

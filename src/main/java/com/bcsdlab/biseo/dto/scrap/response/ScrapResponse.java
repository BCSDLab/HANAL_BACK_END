package com.bcsdlab.biseo.dto.scrap.response;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScrapResponse {
    private Long id;
    private Long userId;
    private Long noticeId;
}

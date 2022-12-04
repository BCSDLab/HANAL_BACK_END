package com.bcsdlab.biseo.dto.notice.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeListDto {

    List<NoticeListItemDTO> noticeList;
    Long nextCursor;
    Boolean isEnd;
}

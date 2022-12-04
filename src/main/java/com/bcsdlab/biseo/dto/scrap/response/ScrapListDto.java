package com.bcsdlab.biseo.dto.scrap.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScrapListDto {

    List<ScrapListItemDTO> scrapList;
    Long nextCursor;
    Boolean isEnd;
}

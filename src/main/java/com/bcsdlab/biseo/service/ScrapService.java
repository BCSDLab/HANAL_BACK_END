package com.bcsdlab.biseo.service;

import com.bcsdlab.biseo.dto.scrap.response.ScrapListDto;
import com.bcsdlab.biseo.dto.scrap.response.ScrapListItemDTO;
import com.bcsdlab.biseo.dto.scrap.response.ScrapResponseDTO;
import java.util.List;

public interface ScrapService {

    ScrapResponseDTO createScrap(Long noticeId);
    void deleteScrap(Long scrapId);

    ScrapListDto getScrapList(String searchBy, Long cursor, Integer limits);
}

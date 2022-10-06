package com.bcsdlab.biseo.repository;

import com.bcsdlab.biseo.dto.scrap.ScrapModel;
import com.bcsdlab.biseo.dto.scrap.response.ScrapListResponseDTO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScrapRepository {

    void createScrap(ScrapModel scrapModel);

    ScrapModel findScrapByUserIdAndNoticeId(Long userId, Long noticeId);
    ScrapModel findScrapById(Long scrapId);
    void deleteScrapById(Long id);
    List<ScrapListResponseDTO> getScrapList(Long userId, String searchBy, Long cursor, Integer limits);
}

package com.bcsdlab.biseo.mapper;

import com.bcsdlab.biseo.dto.scrap.ScrapModel;
import com.bcsdlab.biseo.dto.scrap.response.ScrapResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ScrapMapper {

    ScrapMapper INSTANCE = Mappers.getMapper(ScrapMapper.class);

    ScrapResponseDTO toScrapResponse(ScrapModel scrapModel);
}

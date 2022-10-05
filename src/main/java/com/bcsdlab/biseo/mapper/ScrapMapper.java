package com.bcsdlab.biseo.mapper;

import com.bcsdlab.biseo.dto.scrap.ScrapModel;
import com.bcsdlab.biseo.dto.scrap.response.ScrapResponse;
import com.bcsdlab.biseo.dto.user.response.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ScrapMapper {

    ScrapMapper INSTANCE = Mappers.getMapper(ScrapMapper.class);

    ScrapResponse toScrapResponse(ScrapModel scrapModel);
}

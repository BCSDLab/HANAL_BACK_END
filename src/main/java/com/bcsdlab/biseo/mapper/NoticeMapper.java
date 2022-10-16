package com.bcsdlab.biseo.mapper;

import com.bcsdlab.biseo.dto.notice.model.NoticeAndFileModel;
import com.bcsdlab.biseo.dto.notice.model.NoticeModel;
import com.bcsdlab.biseo.dto.notice.request.NoticeRequestDTO;
import com.bcsdlab.biseo.dto.notice.response.NoticeResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NoticeMapper {

    NoticeMapper INSTANCE = Mappers.getMapper(NoticeMapper.class);

    NoticeModel toNoticeModel(NoticeRequestDTO requestDTO);

    @Mapping(target = "files", ignore = true)
    NoticeResponseDTO toResponseDTO(NoticeAndFileModel model);

    NoticeResponseDTO toResponseDTO2(NoticeModel model);
}

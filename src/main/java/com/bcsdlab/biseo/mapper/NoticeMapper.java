package com.bcsdlab.biseo.mapper;

import com.bcsdlab.biseo.dto.notice.NoticeModel;
import com.bcsdlab.biseo.dto.notice.NoticeRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NoticeMapper {

    NoticeMapper INSTANCE = Mappers.getMapper(NoticeMapper.class);

    NoticeModel toNoticeModel(NoticeRequestDTO requestDTO);
}

package com.bcsdlab.biseo.mapper;

import com.bcsdlab.biseo.dto.user.UserModel;
import com.bcsdlab.biseo.dto.user.UserRequest;
import com.bcsdlab.biseo.dto.user.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "department", ignore = true)
    UserModel toUserModel(UserRequest userRequest);

    @Mapping(target = "department", ignore = true)
    UserResponse toUserResponse(UserModel userModel);
}

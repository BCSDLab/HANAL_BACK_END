package com.bcsdlab.biseo.mapper;

import com.bcsdlab.biseo.dto.user.UserModel;
import com.bcsdlab.biseo.dto.user.UserRequestDTO;
import com.bcsdlab.biseo.dto.user.UserResponseDTO;
import com.bcsdlab.biseo.enums.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;
import org.mapstruct.factory.Mappers;
import org.mindrot.jbcrypt.BCrypt;

@Mapper(componentModel = "spring", imports = {BCrypt.class, Department.class})
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "password", expression = "java( BCrypt.hashpw(userRequestDTO.getPassword(), BCrypt.gensalt()) )")
    @Mapping(target = "department", expression = "java( Department.valueOf(userRequestDTO.getDepartment()).getValue() + userRequestDTO.getGrade() )")
    @Mapping(target = "userType", constant = "NONE")
    UserModel toUserModel(UserRequestDTO userRequestDTO);

    @Mapping(target = "department", ignore = true)
    UserResponseDTO toUserResponse(UserModel userModel);
}

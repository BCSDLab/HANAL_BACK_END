package com.bcsdlab.biseo.mapper;

import com.bcsdlab.biseo.dto.user.model.UserModel;
import com.bcsdlab.biseo.dto.user.request.UserDepartmentDTO;
import com.bcsdlab.biseo.dto.user.request.UserSignUpDTO;
import com.bcsdlab.biseo.dto.user.response.UserResponseDTO;
import com.bcsdlab.biseo.enums.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.mindrot.jbcrypt.BCrypt;

@Mapper(componentModel = "spring", imports = {BCrypt.class, Department.class})
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "password", expression = "java( BCrypt.hashpw(userSignUpDTO.getPassword(), BCrypt.gensalt()) )")
    @Mapping(target = "department", expression = "java( Department.valueOf(userSignUpDTO.getDepartment()).getValue() + userSignUpDTO.getGrade() )")
    @Mapping(target = "userType", constant = "NONE")
    UserModel toUserModel(UserSignUpDTO userSignUpDTO);


    @Mapping(target = "department", expression = "java( Department.valueOf(userDepartmentDTO.getDepartment()).getValue() + userDepartmentDTO.getGrade() )")
    UserModel departmentDtotoUserModel(UserDepartmentDTO userDepartmentDTO);

    @Mapping(target = "department", ignore = true)
    UserResponseDTO toUserResponse(UserModel userModel);
}

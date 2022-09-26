package com.bcsdlab.biseo.service;

import com.bcsdlab.biseo.dto.user.request.UserAuthDTO;
import com.bcsdlab.biseo.dto.user.request.JwtDTO;
import com.bcsdlab.biseo.dto.user.request.UserDepartmentDTO;
import com.bcsdlab.biseo.dto.user.request.UserLoginDTO;
import com.bcsdlab.biseo.dto.user.request.UserPasswordDTO;
import com.bcsdlab.biseo.dto.user.request.UserSignUpDTO;
import com.bcsdlab.biseo.dto.user.response.UserResponseDTO;

public interface UserService {

    UserResponseDTO signUp(UserSignUpDTO request);
    JwtDTO login(UserLoginDTO request);
    String logout();
    JwtDTO refresh(JwtDTO jwtDTO);
    UserAuthDTO sendAuthMail(String accountId);
    String certifySignUpMail(UserAuthDTO userAuthDTO);
    String certifyPasswordMail(UserAuthDTO userAuthDTO);
    UserResponseDTO getMe();
    UserResponseDTO updateDepartment(UserDepartmentDTO request);
    String updatePassword(UserPasswordDTO passwordDTO);
}

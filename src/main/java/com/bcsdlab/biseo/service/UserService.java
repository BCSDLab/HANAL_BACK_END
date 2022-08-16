package com.bcsdlab.biseo.service;

import com.bcsdlab.biseo.dto.user.AuthCodeDTO;
import com.bcsdlab.biseo.dto.user.JwtDTO;
import com.bcsdlab.biseo.dto.user.UserPasswordDTO;
import com.bcsdlab.biseo.dto.user.UserRequestDTO;
import com.bcsdlab.biseo.dto.user.UserResponseDTO;
import java.util.Map;

public interface UserService {

    UserResponseDTO signUp(UserRequestDTO request);
    JwtDTO login(UserRequestDTO request);
    String logout();
    JwtDTO refresh(JwtDTO jwtDTO);
    AuthCodeDTO sendAuthMail(UserRequestDTO request);
    String certifySignUpMail(AuthCodeDTO authCodeDTO);
    String certifyPasswordMail(AuthCodeDTO authCodeDTO);
    UserResponseDTO getMe();
    UserResponseDTO updateDepartment(UserRequestDTO request);
    String updatePassword(UserPasswordDTO passwordDTO);
}

package com.bcsdlab.biseo.service;

import com.bcsdlab.biseo.dto.user.CertificationCodeDTO;
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
    Map<String, String> sendAuthMail(UserRequestDTO request);
    String certifySignUpMail(CertificationCodeDTO certificationCodeDTO);
    String certifyPasswordMail(CertificationCodeDTO certificationCodeDTO);
    UserResponseDTO getMe();
    UserResponseDTO updateDepartment(UserRequestDTO request);
    String updatePassword(UserPasswordDTO passwordDTO);
}

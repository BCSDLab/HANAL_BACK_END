package com.bcsdlab.biseo.service;

import com.bcsdlab.biseo.dto.user.AuthCode;
import com.bcsdlab.biseo.dto.user.AuthDTO;
import com.bcsdlab.biseo.dto.user.UserRequestDTO;
import com.bcsdlab.biseo.dto.user.UserResponseDTO;
import java.util.Map;

public interface UserService {

    UserResponseDTO signUp(UserRequestDTO request);
    AuthDTO login(UserRequestDTO request);
    AuthDTO refresh(AuthDTO authDTO);
    Map<String, Long> sendAuthMail(UserRequestDTO request);
    String verifyAuthMail(AuthCode authCode);
    UserResponseDTO getMe();
    UserResponseDTO updateDepartment(UserRequestDTO request);
}

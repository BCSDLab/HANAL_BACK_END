package com.bcsdlab.biseo.service;

import com.bcsdlab.biseo.dto.user.AuthCode;
import com.bcsdlab.biseo.dto.user.UserRequest;
import com.bcsdlab.biseo.dto.user.UserResponse;
import java.util.Map;

public interface UserService {

    UserResponse signUp(UserRequest request);
    Object login(UserRequest request);
    Map<String, String> refresh();
    Map<String, Long> sendAuthMail(UserRequest request);
    String verifyAuthMail(AuthCode authCode);
    UserResponse getMe();
    UserResponse updateDepartment(UserRequest request);
}

package com.bcsdlab.biseo.service;

import com.bcsdlab.biseo.dto.user.UserRequest;
import com.bcsdlab.biseo.dto.user.UserResponse;
import java.util.Map;

public interface UserService {

    UserResponse signUp(UserRequest request);
}

package com.bcsdlab.biseo.controller;

import com.bcsdlab.biseo.annotation.Auth;
import com.bcsdlab.biseo.annotation.ValidationGroups;
import com.bcsdlab.biseo.dto.user.UserRequest;
import com.bcsdlab.biseo.dto.user.UserResponse;
import com.bcsdlab.biseo.service.UserService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<Map<String, String>> signUp(@RequestBody @Validated(ValidationGroups.SignUp.class) UserRequest request) {
        return new ResponseEntity<>(userService.signUp(request), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Validated(ValidationGroups.Login.class) UserRequest request) {
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    @Auth(type = 2)
    public ResponseEntity<Map<String, String>> refresh() {
        return new ResponseEntity<>(userService.refresh(), HttpStatus.OK);
    }
}

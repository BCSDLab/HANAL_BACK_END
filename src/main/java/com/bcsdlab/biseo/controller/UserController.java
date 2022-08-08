package com.bcsdlab.biseo.controller;

import com.bcsdlab.biseo.annotation.Auth;
import com.bcsdlab.biseo.annotation.ValidationGroups;
import com.bcsdlab.biseo.dto.user.AuthCode;
import com.bcsdlab.biseo.dto.user.UserRequest;
import com.bcsdlab.biseo.dto.user.UserResponse;
import com.bcsdlab.biseo.service.UserService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponse> signUp(@RequestBody @Validated(ValidationGroups.SignUp.class) UserRequest request) {
        return new ResponseEntity<>(userService.signUp(request), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Validated(ValidationGroups.Login.class) UserRequest request) {
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh() {
        return new ResponseEntity<>(userService.refresh(), HttpStatus.OK);
    }

    @PostMapping("/mail-auth")
    public ResponseEntity<Map<String, Long>> sendAuthMail(@RequestBody @Validated(ValidationGroups.Mail.class) UserRequest request) {
        return new ResponseEntity<>(userService.sendAuthMail(request), HttpStatus.OK);
    }

    @PostMapping("/mail-verification")
    public ResponseEntity<String> verifyAuthMail(@RequestBody AuthCode authCode) {
        return new ResponseEntity<>(userService.verifyAuthMail(authCode), HttpStatus.OK);
    }

    @GetMapping("/me")
    @Auth
    public ResponseEntity<UserResponse> getMe() {
        return new ResponseEntity<>(userService.getMe(), HttpStatus.OK);
    }

    @PostMapping("/me/department")
    @Auth
    public ResponseEntity<UserResponse> updateDepartment(@RequestBody @Validated(ValidationGroups.ChangeDepartment.class) UserRequest request) {
        return new ResponseEntity<>(userService.updateDepartment(request), HttpStatus.OK);
    }
}

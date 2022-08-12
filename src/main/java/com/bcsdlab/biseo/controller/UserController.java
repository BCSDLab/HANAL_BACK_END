package com.bcsdlab.biseo.controller;

import com.bcsdlab.biseo.annotation.Auth;
import com.bcsdlab.biseo.annotation.ValidationGroups;
import com.bcsdlab.biseo.dto.user.AuthCode;
import com.bcsdlab.biseo.dto.user.AuthDTO;
import com.bcsdlab.biseo.dto.user.UserRequestDTO;
import com.bcsdlab.biseo.dto.user.UserResponseDTO;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDTO> signUp(@RequestBody @Validated(ValidationGroups.SignUp.class) UserRequestDTO request) {
        return new ResponseEntity<>(userService.signUp(request), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTO> login(@RequestBody @Validated(ValidationGroups.Login.class) UserRequestDTO request) {
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

    @PostMapping("/logout")
    @Auth
    public ResponseEntity<String> logout() {
        return new ResponseEntity<>(userService.logout(), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthDTO> refresh(@RequestBody AuthDTO authDTO) {
        return new ResponseEntity<>(userService.refresh(authDTO), HttpStatus.OK);
    }

    @PostMapping("/send-mail")
    public ResponseEntity<Map<String, Long>> sendAuthMail(@RequestBody @Validated(ValidationGroups.Mail.class) UserRequestDTO request) {
        return new ResponseEntity<>(userService.sendAuthMail(request), HttpStatus.OK);
    }

    @PostMapping("/verify-mail")
    public ResponseEntity<String> verifyAuthMail(@RequestBody AuthCode authCode) {
        return new ResponseEntity<>(userService.verifyAuthMail(authCode), HttpStatus.OK);
    }

    @GetMapping("/me")
    @Auth
    public ResponseEntity<UserResponseDTO> getMe() {
        return new ResponseEntity<>(userService.getMe(), HttpStatus.OK);
    }

    @PostMapping("/me/department")
    @Auth
    public ResponseEntity<UserResponseDTO> updateDepartment(@RequestBody @Validated(ValidationGroups.ChangeDepartment.class) UserRequestDTO request) {
        return new ResponseEntity<>(userService.updateDepartment(request), HttpStatus.OK);
    }
}

package com.bcsdlab.biseo.controller;

import com.bcsdlab.biseo.annotation.Auth;
import com.bcsdlab.biseo.annotation.ValidationGroups;
import com.bcsdlab.biseo.dto.user.AuthCodeDTO;
import com.bcsdlab.biseo.dto.user.JwtDTO;
import com.bcsdlab.biseo.dto.user.UserPasswordDTO;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signUp(@RequestBody @Validated(ValidationGroups.SignUp.class) UserRequestDTO request) {
        return new ResponseEntity<>(userService.signUp(request), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDTO> login(@RequestBody @Validated(ValidationGroups.Login.class) UserRequestDTO request) {
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

    @PostMapping("/logout")
    @Auth
    public ResponseEntity<String> logout() {
        return new ResponseEntity<>(userService.logout(), HttpStatus.NO_CONTENT);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtDTO> refresh(@RequestBody JwtDTO jwtDTO) {
        return new ResponseEntity<>(userService.refresh(jwtDTO), HttpStatus.OK);
    }

    @PostMapping("/send-mail")
    public ResponseEntity<AuthCodeDTO> sendAuthMail(@RequestBody @Validated(ValidationGroups.Mail.class) UserRequestDTO request) {
        return new ResponseEntity<>(userService.sendAuthMail(request), HttpStatus.OK);
    }

    @PostMapping("/certify-signup")
    public ResponseEntity<String> certifySignUpMail(@RequestBody AuthCodeDTO authCodeDTO) {
        return new ResponseEntity<>(userService.certifySignUpMail(authCodeDTO), HttpStatus.OK);
    }

    @PostMapping("/certify-password")
    public ResponseEntity<String> certifyPasswordMail(@RequestBody AuthCodeDTO authCodeDTO) {
        return new ResponseEntity<>(userService.certifyPasswordMail(authCodeDTO), HttpStatus.OK);
    }

    @GetMapping("/me")
    @Auth
    public ResponseEntity<UserResponseDTO> getMe() {
        return new ResponseEntity<>(userService.getMe(), HttpStatus.OK);
    }

    @PutMapping("/me/department")
    @Auth
    public ResponseEntity<UserResponseDTO> updateDepartment(@RequestBody @Validated(ValidationGroups.ChangeDepartment.class) UserRequestDTO request) {
        return new ResponseEntity<>(userService.updateDepartment(request), HttpStatus.OK);
    }

    @PutMapping("/me/password")
    @Auth
    public ResponseEntity<String> updatePassword(@RequestBody UserPasswordDTO passwordDTO) {
        return new ResponseEntity<>(userService.updatePassword(passwordDTO), HttpStatus.OK);
    }
}

package com.bcsdlab.biseo.controller;

import com.bcsdlab.biseo.annotation.Auth;
import com.bcsdlab.biseo.annotation.ValidationGroups;
import com.bcsdlab.biseo.dto.user.AuthCodeDTO;
import com.bcsdlab.biseo.dto.user.JwtDTO;
import com.bcsdlab.biseo.dto.user.UserPasswordDTO;
import com.bcsdlab.biseo.dto.user.UserRequestDTO;
import com.bcsdlab.biseo.dto.user.UserResponseDTO;
import com.bcsdlab.biseo.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
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
    @ApiOperation(
        value = "회원가입",
        notes = "회원가입")
    public ResponseEntity<UserResponseDTO> signUp(
        @RequestBody
        @Validated(ValidationGroups.SignUp.class)
        @ApiParam(name = "request", value = "require : all", type = "UserRequestDTO" )
        UserRequestDTO request) {
        return new ResponseEntity<>(userService.signUp(request), HttpStatus.OK);
    }

    @PostMapping("/login")
    @ApiOperation(
        value = "로그인",
        notes = "로그인")
    public ResponseEntity<JwtDTO> login(
        @RequestBody
        @Validated(ValidationGroups.Login.class)
        @ApiParam(name = "request", value = "require : accountId, password", type = "UserRequestDTO" )
        UserRequestDTO request) {
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

    @PostMapping("/logout")
    @Auth
    @ApiOperation(
        value = "로그아웃",
        notes = "로그아웃",
        authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<String> logout() {
        return new ResponseEntity<>(userService.logout(), HttpStatus.NO_CONTENT);
    }

    @PostMapping("/refresh")
    @ApiOperation(
        value = "access 토큰 재발급",
        notes = "access 토큰과 refresh 토큰을 사용하여 만료된 access 토큰을 재발급 받는다.")
    public ResponseEntity<JwtDTO> refresh(
        @RequestBody
        @ApiParam(name = "jwtDto", value = "require : access, refresh", type = "JwtDTO" )
        JwtDTO jwtDTO) {
        return new ResponseEntity<>(userService.refresh(jwtDTO), HttpStatus.OK);
    }

    @PostMapping("/send-mail")
    @ApiOperation(
        value = "인증메일 전송",
        notes = "회원가입 인증, 비밀번호 찾기 인증 메일 전송")
    public ResponseEntity<AuthCodeDTO> sendAuthMail(
        @RequestBody
        @Validated(ValidationGroups.Mail.class)
        @ApiParam(name = "request", value = "require : accountId", type = "UserRequestDTO" )
        UserRequestDTO request) {
        return new ResponseEntity<>(userService.sendAuthMail(request), HttpStatus.OK);
    }

    @PostMapping("/certify-signup")
    @ApiOperation(
        value = "회원가입 인증",
        notes = "회원가입 인증용으로 보낸 코드 인증")
    public ResponseEntity<String> certifySignUpMail(
        @RequestBody
        @ApiParam(name = "authCodeDTO", value = "require : accountId, authCode", type = "AuthCodeDTO" )
        AuthCodeDTO authCodeDTO) {
        return new ResponseEntity<>(userService.certifySignUpMail(authCodeDTO), HttpStatus.OK);
    }

    @PostMapping("/certify-password")
    @ApiOperation(
        value = "비밀번호 찾기 인증",
        notes = "비밀번호 찾기 인증용으로 보낸 코드 인증")
    public ResponseEntity<String> certifyPasswordMail(
        @RequestBody
        @ApiParam(name = "authCodeDTO", value = "require : accountId, authCode", type = "AuthCodeDTO" )
        AuthCodeDTO authCodeDTO) {
        return new ResponseEntity<>(userService.certifyPasswordMail(authCodeDTO), HttpStatus.OK);
    }

    @GetMapping("/me")
    @Auth
    @ApiOperation(
        value = "내 정보",
        notes = "내 정보를 불러온다.",
        authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<UserResponseDTO> getMe() {
        return new ResponseEntity<>(userService.getMe(), HttpStatus.OK);
    }

    @PutMapping("/me/department")
    @Auth
    @ApiOperation(
        value = "학과/학년 수정",
        notes = "내 학과/학년을 수정합니다.",
        authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<UserResponseDTO> updateDepartment(
        @RequestBody
        @Validated(ValidationGroups.ChangeDepartment.class)
        @ApiParam(name = "request", value = "require : department, grade", type = "UserRequestDTO" )
        UserRequestDTO request) {
        return new ResponseEntity<>(userService.updateDepartment(request), HttpStatus.OK);
    }

    @PutMapping("/me/password")
    @Auth
    @ApiOperation(
        value = "비밀번호 변경",
        notes = "비밀번호를 변경합니다.",
        authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<String> updatePassword(
        @RequestBody
        @ApiParam(name = "request", value = "require : password", type = "UserRequestDTO" )
        UserPasswordDTO passwordDTO) {
        return new ResponseEntity<>(userService.updatePassword(passwordDTO), HttpStatus.OK);
    }
}

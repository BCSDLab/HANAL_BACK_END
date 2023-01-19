package com.bcsdlab.biseo.controller;

import com.bcsdlab.biseo.annotation.Auth;
import com.bcsdlab.biseo.annotation.ValidationGroups;
import com.bcsdlab.biseo.dto.user.request.JwtDTO;
import com.bcsdlab.biseo.dto.user.request.UserAuthDTO;
import com.bcsdlab.biseo.dto.user.request.UserDepartmentDTO;
import com.bcsdlab.biseo.dto.user.request.UserLoginDTO;
import com.bcsdlab.biseo.dto.user.request.UserPasswordDTO;
import com.bcsdlab.biseo.dto.user.request.UserSignUpDTO;
import com.bcsdlab.biseo.dto.user.response.UserResponseDTO;
import com.bcsdlab.biseo.enums.AuthType;
import com.bcsdlab.biseo.service.UserService;
import io.swagger.annotations.ApiOperation;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @ApiOperation(value = "회원가입",
        notes = "회원 가입.\n\n"
            + "학과 목록 : \n\n"
            + "    기계공학부(1000),\n\n"
            + "    메카트로닉스공학부(2000),\n\n"
            + "    전기공학과(3000),\n\n"
            + "    전자공학과(3100),\n\n"
            + "    정보통신공학과(3200),\n\n"
            + "    컴퓨터공학부(4000),\n\n"
            + "    건축공학과(5000),\n\n"
            + "    응용화학공학과(6000),\n\n"
            + "    에너지신소재공학과(6100),\n\n"
            + "    산업경영학부(7000),\n\n"
            + "    고용서비스정책학과(8000),\n\n"
            + "    전체(9000)")
    public ResponseEntity<UserResponseDTO> signUp(
        @RequestBody @Validated(ValidationGroups.SignUp.class) UserSignUpDTO request) {
        return new ResponseEntity<>(userService.signUp(request), HttpStatus.OK);
    }

    @PostMapping("/login")
    @ApiOperation(value = "로그인", notes = "로그인")
    public ResponseEntity<JwtDTO> login(
        @RequestBody @Validated(ValidationGroups.Login.class) UserLoginDTO request) {
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

    @PostMapping("/logout")
    @Auth
    @ApiOperation(value = "로그아웃", notes = "로그아웃", authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<Void> logout() {
        userService.logout();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/refresh")
    @ApiOperation(value = "access 토큰 재발급",
        notes = "refresh 토큰을 사용하여 만료된 access 토큰을 재발급 받는다.\n\n"
            + "헤더 : Authorization => Bearer RefreshToken",
        authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<JwtDTO> refresh() {
        return new ResponseEntity<>(userService.refresh(), HttpStatus.OK);
    }

    @PostMapping("/signup/send-mail")
    @ApiOperation(value = "회원가입 인증메일 전송", notes = "회원가입 인증 메일 전송")
    public ResponseEntity<UserAuthDTO> sendAuthMail(@RequestParam(value = "accountId") String accountId) {
        return new ResponseEntity<>(userService.sendAuthMail(accountId, AuthType.SIGNUP), HttpStatus.OK);
    }

    @PostMapping("/password/send-mail")
    @ApiOperation(value = "비밀번호 찾기 메일 전송", notes = "비밀번호 찾기 인증 메일 전송")
    public ResponseEntity<UserAuthDTO> sendPasswordMail(@RequestParam(value = "accountId") String accountId) {
        return new ResponseEntity<>(userService.sendAuthMail(accountId, AuthType.PASSWORD), HttpStatus.OK);
    }

    @PostMapping("/signup/authorize")
    @ApiOperation(value = "회원가입 이메일 코드 인증", notes = "회원가입 인증용으로 보낸 코드 인증")
    public ResponseEntity<Void> authorizeSignUpMail(@RequestBody UserAuthDTO userAuthDTO) {
        userService.authorizeSignUpMail(userAuthDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/password/authorize")
    @ApiOperation(value = "비밀번호 찾기 이메일 코드 인증", notes = "비밀번호 찾기 인증용으로 보낸 코드 인증. 인증 후 비밀번호 해당 메일로 전송")
    public ResponseEntity<Void> authorizePasswordMail(@RequestBody UserAuthDTO userAuthDTO) {
        userService.authorizePasswordMail(userAuthDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/me")
    @Auth
    @ApiOperation(value = "내 정보", notes = "내 정보를 불러온다.", authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<UserResponseDTO> getMe() {
        return new ResponseEntity<>(userService.getMe(), HttpStatus.OK);
    }

    @PutMapping("/me/department")
    @Auth
    @ApiOperation(value = "학과/학년 수정",
        notes = "내 학과/학년을 수정합니다.\n\n"
            + "학과 목록 : \n\n"
            + "    기계공학부(1000),\n\n"
            + "    메카트로닉스공학부(2000),\n\n"
            + "    전기공학과(3000),\n\n"
            + "    전자공학과(3100),\n\n"
            + "    정보통신공학과(3200),\n\n"
            + "    컴퓨터공학부(4000),\n\n"
            + "    건축공학과(5000),\n\n"
            + "    응용화학공학과(6000),\n\n"
            + "    에너지신소재공학과(6100),\n\n"
            + "    산업경영학부(7000),\n\n"
            + "    고용서비스정책학과(8000),\n\n"
            + "    전체(9000)",
        authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<UserResponseDTO> updateDepartment(
        @RequestBody UserDepartmentDTO request) {
        return new ResponseEntity<>(userService.updateDepartment(request), HttpStatus.OK);
    }

    @PutMapping("/me/password")
    @Auth
    @ApiOperation(value = "비밀번호 변경", notes = "비밀번호를 변경합니다.", authorizations = {@Authorization(value = "Authorization")})
    public ResponseEntity<Void> updatePassword(@RequestBody UserPasswordDTO passwordDTO) {
        userService.updatePassword(passwordDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

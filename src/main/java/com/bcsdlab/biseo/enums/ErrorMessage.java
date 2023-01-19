package com.bcsdlab.biseo.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorMessage {
    USER_ID_EXIST("존재하는 계정입니다.", HttpStatus.BAD_REQUEST),
    USER_ID_NOT_EXIST("존재하지 않는 계정입니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_AUTHORIZATION("인증되지 않은 사용자입니다. 인증을 진행해주세요.", HttpStatus.UNAUTHORIZED),
    USER_NOT_EXIST("유저가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    SEND_MAIL_FAIL("메일 전송이 불가능합니다.", HttpStatus.BAD_REQUEST),
    SEND_MAIL_FIRST("이메일 전송을 먼저 해주세요", HttpStatus.BAD_REQUEST),
    AUTH_CODE_INVALID("인증번호가 다릅니다. 인증번호를 확인해주세요", HttpStatus.BAD_REQUEST),
    CHANGE_PASSWORD_FAIL("기존 비밀번호가 다릅니다.",HttpStatus.BAD_REQUEST),
    ACCESS_TOKEN_INVALID("올바르지 않은 토큰입니다.", HttpStatus.BAD_REQUEST),
    ACCESS_TOKEN_EXPIRED("토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID("올바르지 않은 토큰입니다.", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_EXPIRED("리프레시 토큰이 유효하지 않습니다. 다시 로그인하세요.", HttpStatus.UNAUTHORIZED),
    NO_AUTHORIZATION("해당 서비스에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),
    GRADE_REQUIRED("학년을 선택해야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_NOTICE_ID("잘못된 접근입니다.", HttpStatus.BAD_REQUEST),
    NOTICE_NOT_FOUND("존재하지 않는 공지입니다.", HttpStatus.NOT_FOUND),
    FILE_UPLOAD_FAIL("파일 업로드 중 문제가 발생하였습니다.", HttpStatus.BAD_REQUEST),
    SCRAP_DENIED("스크랩 권한이 없습니다.", HttpStatus.UNAUTHORIZED),
    SCRAP_DELETE_DENIED("스크랩 삭제 권한이 없습니다.", HttpStatus.UNAUTHORIZED),
    SCRAP_EXIST("스크랩이 이미 존재합니다.", HttpStatus.BAD_REQUEST),
    SCRAP_NOT_EXIST("스크랩이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    LIMITS_NOT_VALID("리미트 값은 0보다 커야 합니다.", HttpStatus.BAD_REQUEST),
    ;


    final String message;
    final HttpStatus httpStatus;

    ErrorMessage(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}

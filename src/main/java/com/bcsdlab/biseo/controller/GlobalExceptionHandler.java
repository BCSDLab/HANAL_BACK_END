package com.bcsdlab.biseo.controller;

import com.bcsdlab.biseo.dto.ErrorResponse;
import com.bcsdlab.biseo.exception.BaseException;
import com.bcsdlab.biseo.exception.CriticalException;
import com.bcsdlab.biseo.exception.NonCriticalException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> catchValidationException(MethodArgumentNotValidException e) {
        Map<String, String> message = new HashMap<>();
        List<ObjectError> errorList = e.getAllErrors();
        for (ObjectError error : errorList) {
            message.put(((FieldError) error).getField(), error.getDefaultMessage());
        }

        ErrorResponse response = new ErrorResponse();
        response.setClassName(e.getClass().getSimpleName());
        response.setErrorMessage("데이터가 유효하지 않습니다.");
        response.setErrorMessages(message);
        response.setHttpStatus(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NonCriticalException.class)
    public ResponseEntity<ErrorResponse> catchNonCriticalException(NonCriticalException e) {
        return new ResponseEntity<>(e.getResponse(), e.getHttpStatus());
    }

    @ExceptionHandler(value = CriticalException.class)
    public ResponseEntity<ErrorResponse> catchCriticalException(CriticalException e) {
        // TODO : slack noti....
        return new ResponseEntity<>(e.getResponse(), e.getHttpStatus());
    }
}

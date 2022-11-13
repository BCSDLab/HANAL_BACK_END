package com.bcsdlab.biseo.controller;

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

    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<Map<String, String>> catchValidationException(BindException e, HandlerMethod handlerMethod) {
        Map<String, String> response = new HashMap<>();
        List<ObjectError> errorList = e.getAllErrors();

        for (ObjectError error : errorList) {
            response.put(((FieldError) error).getField(), error.getDefaultMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}

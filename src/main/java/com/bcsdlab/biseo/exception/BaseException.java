package com.bcsdlab.biseo.exception;

import com.bcsdlab.biseo.dto.ErrorResponse;
import com.bcsdlab.biseo.enums.ErrorMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BaseException extends RuntimeException{
    protected String className;
    protected String errorMessage;
    protected HttpStatus httpStatus;

    public BaseException(ErrorMessage errorMessage){
        this.className = this.getClass().getSimpleName();
        this.errorMessage = errorMessage.getMessage();
        this.httpStatus = errorMessage.getHttpStatus();
    }

    public ErrorResponse getResponse() {
        ErrorResponse response = new ErrorResponse();

        response.setClassName(this.className);
        response.setErrorMessage(this.errorMessage);
        response.setHttpStatus(this.httpStatus);

        return response;
    }
}

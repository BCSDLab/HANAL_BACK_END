package com.bcsdlab.biseo.exception;

import com.bcsdlab.biseo.enums.ErrorMessage;

public class BadRequestException extends NonCriticalException{

    public BadRequestException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}

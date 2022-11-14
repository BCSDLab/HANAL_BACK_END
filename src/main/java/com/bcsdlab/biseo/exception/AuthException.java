package com.bcsdlab.biseo.exception;

import com.bcsdlab.biseo.enums.ErrorMessage;

public class AuthException extends NonCriticalException{

    public AuthException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}

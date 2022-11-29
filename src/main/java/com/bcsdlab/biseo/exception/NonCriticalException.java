package com.bcsdlab.biseo.exception;

import com.bcsdlab.biseo.enums.ErrorMessage;

public class NonCriticalException extends BaseException{

    public NonCriticalException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}

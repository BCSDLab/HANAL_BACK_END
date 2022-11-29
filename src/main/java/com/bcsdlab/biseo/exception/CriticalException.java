package com.bcsdlab.biseo.exception;

import com.bcsdlab.biseo.enums.ErrorMessage;

public class CriticalException extends BaseException{

    public CriticalException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}

package com.bcsdlab.biseo.exception;

import com.bcsdlab.biseo.enums.ErrorMessage;

public class NotFoundException extends NonCriticalException {

    public NotFoundException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}

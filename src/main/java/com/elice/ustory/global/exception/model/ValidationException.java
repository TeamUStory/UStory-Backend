package com.elice.ustory.global.exception.model;

import com.elice.ustory.global.exception.ErrorCode;

public class ValidationException extends CustomException {
    public ValidationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ValidationException(String message) {
        super(message, ErrorCode.VALIDATION_EXCEPTION);
    }
}

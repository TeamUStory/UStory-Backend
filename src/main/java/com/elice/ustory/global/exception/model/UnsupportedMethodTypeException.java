package com.elice.ustory.global.exception.model;

import com.elice.ustory.global.exception.ErrorCode;

public class UnsupportedMethodTypeException extends CustomException {
    public UnsupportedMethodTypeException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public UnsupportedMethodTypeException(String message) {
        super(message, ErrorCode.METHOD_NOT_ALLOWED_EXCEPTION);
    }
}

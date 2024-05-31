package com.elice.ustory.global.exception.model;

import com.elice.ustory.global.exception.ErrorCode;

public class UnauthorizedException extends CustomException {
    public UnauthorizedException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public UnauthorizedException(String message) {
        super(message, ErrorCode.UNAUTHORIZED_EXCEPTION);
    }
}

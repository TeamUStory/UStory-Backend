package com.elice.ustory.global.exception.model;

import com.elice.ustory.global.exception.ErrorCode;

public class ForbiddenException extends CustomException {
    public ForbiddenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ForbiddenException(String message) {
        super(message, ErrorCode.FORBIDDEN_EXCEPTION);
    }
}

package com.elice.ustory.global.exception.model;

import com.elice.ustory.global.exception.ErrorCode;

public class ConflictException extends CustomException {
    public ConflictException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ConflictException(String message) {
        super(message, ErrorCode.CONFLICT_EXCEPTION);
    }
}

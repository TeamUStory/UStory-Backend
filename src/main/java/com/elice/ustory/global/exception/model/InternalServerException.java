package com.elice.ustory.global.exception.model;

import com.elice.ustory.global.exception.ErrorCode;

public class InternalServerException extends CustomException {
    public InternalServerException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public InternalServerException(String message) {
        super(message, ErrorCode.INTERNAL_SERVER_EXCEPTION);
    }
}

package com.elice.ustory.global.exception.model;

import com.elice.ustory.global.exception.ErrorCode;

public class AccessTokenExpiredException extends CustomException{
    public AccessTokenExpiredException(String message) {
        super(message, ErrorCode.EXPIRED_TOKEN_EXCEPTION);
    }
}

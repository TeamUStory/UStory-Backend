package com.elice.ustory.global.exception.model;

import com.elice.ustory.global.exception.ErrorCode;

public class RefreshTokenExpiredException extends CustomException{
    public RefreshTokenExpiredException(String message){
        super(message, ErrorCode.EXPIRED_REFRESH_TOKEN_EXCEPTION);
    }
}

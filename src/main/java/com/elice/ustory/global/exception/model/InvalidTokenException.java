package com.elice.ustory.global.exception.model;

import com.elice.ustory.global.exception.ErrorCode;

public class InvalidTokenException extends CustomException{
    public InvalidTokenException(String message){
        super(message, ErrorCode.INVALID_TOKEN_EXCEPTION);
    }
}

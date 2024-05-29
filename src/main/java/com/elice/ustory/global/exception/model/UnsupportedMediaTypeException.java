package com.elice.ustory.global.exception.model;

import com.elice.ustory.global.exception.ErrorCode;

public class UnsupportedMediaTypeException extends CustomException {
    public UnsupportedMediaTypeException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public UnsupportedMediaTypeException(String message) {
        super(message, ErrorCode.UNSUPPORTED_MEDIA_TYPE_EXCEPTION);
    }
}

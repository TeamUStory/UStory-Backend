package com.elice.ustory.global.exception.dto;

import com.elice.ustory.global.exception.model.CustomException;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String errorCode;
    private final String message;
    private final String detail_message;

    public ErrorResponse(CustomException ex) {
        this.errorCode = ex.getErrorCode().getCode();
        this.message = ex.getErrorCode().getMessage();
        this.detail_message = ex.getMessage();
    }

    public ErrorResponse(CustomException ex, String message) {
        this.errorCode = ex.getErrorCode().getCode();
        this.message = ex.getErrorCode().getMessage();
        this.detail_message = message;
    }

}

package com.elice.ustory.global.exception.dto;

import com.elice.ustory.global.exception.model.CustomException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ErrorResponse {

    @Schema(description = "에러 코드", example = "V001")
    private final String errorCode;

    @Schema(description = "에러 메세지", example = "잘못된 요청입니다.")
    private final String message;

    @Schema(description = "에러 디테일 메세지", example = "해당하는 친구가 존재하지 않습니다.")
    private final String detailMessage;

    public ErrorResponse(CustomException ex) {
        this.errorCode = ex.getErrorCode().getCode();
        this.message = ex.getErrorCode().getMessage();
        this.detailMessage = ex.getMessage();
    }

    public ErrorResponse(CustomException ex, String message) {
        this.errorCode = ex.getErrorCode().getCode();
        this.message = ex.getErrorCode().getMessage();
        this.detailMessage = message;
    }

}

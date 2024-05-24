package com.elice.ustory.domain.user.dto;

import org.springframework.http.ResponseEntity;

public class SignUpResponse extends ResponseDto {

    private SignUpResponse()  {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
    }

    public static ResponseEntity<SignUpResponse> success() {
        return ResponseEntity.ok(new SignUpResponse());
    }
}

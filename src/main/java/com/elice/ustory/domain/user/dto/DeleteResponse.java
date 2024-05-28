package com.elice.ustory.domain.user.dto;

import com.elice.ustory.domain.user.dto.common.ResponseCode;
import com.elice.ustory.domain.user.dto.common.ResponseDto;
import com.elice.ustory.domain.user.dto.common.ResponseMessage;
import org.springframework.http.ResponseEntity;

public class DeleteResponse extends ResponseDto {

    private DeleteResponse() { super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);}

    public static ResponseEntity<DeleteResponse> success() { return ResponseEntity.ok(new DeleteResponse());
    }
}

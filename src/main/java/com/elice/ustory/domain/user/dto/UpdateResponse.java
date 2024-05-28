package com.elice.ustory.domain.user.dto;

import com.elice.ustory.domain.user.dto.common.ResponseCode;
import com.elice.ustory.domain.user.dto.common.ResponseDto;
import com.elice.ustory.domain.user.dto.common.ResponseMessage;
import org.springframework.http.ResponseEntity;

public class UpdateResponse extends ResponseDto {

    private UpdateResponse()  {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
    }

    public static ResponseEntity<UpdateResponse> success() {
        return ResponseEntity.ok(new UpdateResponse());
    }
}

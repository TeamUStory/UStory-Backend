package com.elice.ustory.domain.user.dto;

import com.elice.ustory.domain.user.dto.common.ResponseCode;
import com.elice.ustory.domain.user.dto.common.ResponseDto;
import com.elice.ustory.domain.user.dto.common.ResponseMessage;
import org.springframework.http.ResponseEntity;

public class ReadResponse extends ResponseDto {

    private ReadResponse() { super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);}

    public static ResponseEntity<ReadResponse> success() { return ResponseEntity.ok(new ReadResponse());
    }
}

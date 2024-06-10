package com.elice.ustory.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Data //TODO: 불필요한 @Data 삭제
@Builder
public class AuthCodeCreateResponse {
    Boolean isSuccess;
    String fromMail;
    String toMail;
    String title;
    String authCode;
}

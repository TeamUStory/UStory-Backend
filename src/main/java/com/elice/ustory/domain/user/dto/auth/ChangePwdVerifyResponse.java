package com.elice.ustory.domain.user.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangePwdVerifyResponse {
    String accessToken;
    Boolean isValid;
    String message;
}

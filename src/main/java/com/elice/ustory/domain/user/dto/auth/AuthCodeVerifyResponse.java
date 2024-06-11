package com.elice.ustory.domain.user.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthCodeVerifyResponse {
    Boolean isValid;
    String message;
}

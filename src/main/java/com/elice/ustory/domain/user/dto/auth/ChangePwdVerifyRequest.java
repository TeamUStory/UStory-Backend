package com.elice.ustory.domain.user.dto.auth;

import jakarta.validation.constraints.Email;

public class ChangePwdVerifyRequest {
    @Email
    private String toEmail;
    private String authCode;
}

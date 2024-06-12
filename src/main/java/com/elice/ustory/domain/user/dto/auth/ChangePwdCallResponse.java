package com.elice.ustory.domain.user.dto.auth;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangePwdCallResponse {
    String message;

    String fromEmail;

    String toEmail;

    String title;
    String authCode;
}

package com.elice.ustory.domain.user.dto.auth;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class ChangePwdCallRequest {
    @Email
    String toEmail;
}
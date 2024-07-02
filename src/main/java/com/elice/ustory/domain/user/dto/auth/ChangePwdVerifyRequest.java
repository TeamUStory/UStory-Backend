package com.elice.ustory.domain.user.dto.auth;

import com.elice.ustory.domain.user.constant.RegexPatterns;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class ChangePwdVerifyRequest {
    @Pattern(regexp = RegexPatterns.EMAIL_REG, message = "이메일 형식이 올바르지 않습니다.")
    private String toEmail;
    private String authCode;
}

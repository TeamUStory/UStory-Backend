package com.elice.ustory.domain.user.dto;

import com.elice.ustory.domain.user.constant.RegexPatterns;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequest {
    @Pattern(regexp = RegexPatterns.EMAIL_REG, message = "이메일 형식이 올바르지 않습니다.")
    private String loginEmail;
    private String password;
}

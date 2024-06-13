package com.elice.ustory.domain.user.dto.auth;

import com.elice.ustory.domain.user.entity.RegexPatterns;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthCodeCreateRequest {
    @Pattern(regexp = RegexPatterns.EMAIL_REG, message = "이메일 형식이 올바르지 않습니다.")
    private String email;
}

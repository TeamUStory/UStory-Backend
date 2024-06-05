package com.elice.ustory.domain.user.dto;

import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthCodeCreateRequest {
    @Email // 이메일 형식 검증
    private String email;
}

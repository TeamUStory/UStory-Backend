package com.elice.ustory.domain.user.dto;

import com.elice.ustory.domain.user.entity.RegexPatterns;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class ChangePwdRequest {
    @NotEmpty(message = "비밀번호 입력란은 필수 입력값입니다.")
    @Pattern(regexp = RegexPatterns.PASSWORD_REG, message = "비밀번호 형식이 올바르지 않습니다.")
    String password;

    @NotEmpty(message = "비밀번호 확인란은 필수 입력값입니다.")
    String passwordCheck;
}

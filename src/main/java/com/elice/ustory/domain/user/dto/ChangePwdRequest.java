package com.elice.ustory.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class ChangePwdRequest {
    @NotEmpty(message = "비밀번호 입력란은 필수 입력값입니다.")
    String password;

    @NotEmpty(message = "비밀번호 확인란은 필수 입력값입니다.")
    String passwordCheck;
}

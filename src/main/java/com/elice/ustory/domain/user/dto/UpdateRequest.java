package com.elice.ustory.domain.user.dto;

import com.elice.ustory.domain.user.entity.RegexPatterns;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateRequest {
    private String name;
    private String nickname;

    @Pattern(regexp = RegexPatterns.PASSWORD_REG, message = "비밀번호 형식이 올바르지 않습니다.")
    private String password;

    private String profileImgUrl;
    private String profileDescription;
}

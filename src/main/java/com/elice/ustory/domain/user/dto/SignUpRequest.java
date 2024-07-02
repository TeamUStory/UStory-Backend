package com.elice.ustory.domain.user.dto;

import com.elice.ustory.domain.user.constant.RegexPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
public class SignUpRequest {
    @NotEmpty
    @Pattern(regexp = RegexPatterns.EMAIL_REG, message = "이메일 형식이 올바르지 않습니다.")
    @Schema(requiredMode = REQUIRED, example = "example@email.com")
    private String email; // 인증 여부: done

    @NotEmpty
    @Schema(requiredMode = REQUIRED, description = "빈 스트링은 허용되지 않습니다.", example = "피카츄")
    private String name; // 인증 여부: to do

    @NotEmpty
    @Schema(requiredMode = REQUIRED, example = "Nick")
    private String nickname; // 인증 여부: done

    @NotEmpty
    @Pattern(regexp = RegexPatterns.PASSWORD_REG, message = "비밀번호 형식이 올바르지 않습니다.")
    @Schema(requiredMode = REQUIRED, description = "형식 조건에 유의하세요.", example = "password!!11")
    private String password; // 인증 여부: to do

    @NotEmpty
    @Schema(requiredMode = REQUIRED, description = "첫번째 입력한 비밀번호와 같은 값을 입력해야 합니다.", example = "password!!11")
    private String passwordCheck;

    @NotEmpty(message = "이메일은 필수 입력값입니다.")
    @Schema(requiredMode = REQUIRED, description = "회원가입 시 기본값을 입력받습니다.", example = "../images/profile.png")
    private String profileImgUrl; // 가입 시 기본값 입력(프론트에서 전달)

    @NotEmpty
    @Schema(requiredMode = REQUIRED, description = "회원가입 시 기본값을 입력받습니다.", example = "자기소개를 작성하지 않았습니다.")
    private String profileDescription; // 가입 시 기본값 입력(프론트에서 전달)

    @NotEmpty
    @Schema(requiredMode = REQUIRED, description = "회원가입 시 기본값을 입력받습니다.", example = "../images/diary.png")
    private String diaryImgUrl; // 가입 시 기본값 입력(프론트에서 전달)
}

package com.elice.ustory.domain.user.dto;

import lombok.Data;

@Data
public class SignUpRequest {
    private String email; // 인증 여부: done
    private String name; // 인증 여부: to do
    private String nickname; // 인증 여부: done
    private String password; // 인증 여부: to do
    private String passwordCheck;
    private String profileImgUrl; // 가입 시 기본값 입력(프론트에서 전달)
    private String profileDescription; // 가입 시 기본값 입력(프론트에서 전달)
    private String diaryImgUrl; // 가입 시 기본값 입력(프론트에서 전달)
}

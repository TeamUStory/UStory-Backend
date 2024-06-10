package com.elice.ustory.domain.user.dto;

import lombok.Data;

@Data
public class ValidateNicknameRequest {
    // TODO: 닉네임 규칙 생길 경우, 별도 메서드 없이 dto에서 확인
    private String nickname;
}

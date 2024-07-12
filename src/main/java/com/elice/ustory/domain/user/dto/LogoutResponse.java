package com.elice.ustory.domain.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class LogoutResponse {
    private Boolean success;
    private String loginType;

    public LogoutResponse(Boolean success, String loginType) {
        this.success = success;
        this.loginType = loginType;
    }
}

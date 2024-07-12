package com.elice.ustory.global.jwt;

import lombok.Getter;

@Getter
public class JwtRefreshResponse {
    private final String refreshAccessToken;

    public JwtRefreshResponse(String refreshAccessToken) {
        this.refreshAccessToken = refreshAccessToken;
    }
}

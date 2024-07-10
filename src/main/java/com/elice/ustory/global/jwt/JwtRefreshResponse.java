package com.elice.ustory.global.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class JwtRefreshResponse {
    private String refreshAccessToken;

    public JwtRefreshResponse(String refreshAccessToken) {
        this.refreshAccessToken = refreshAccessToken;
    }
}

package com.elice.ustory.global.oauth.naver;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Getter
public class NaverOauth {
    @Value("${naver.clientId}")
    private String naverClientId;

    @Value("${naver.secret}")
    private String naverSecretKey;

    @Value("${naver.loginRedirectUri}")
    private String naverLoginRedirectUri;

    @Value("${naver.logoutRedirectUri}")
    private String naverLogoutRedirectUri;

    @Value("${naver.requestTokenUri}")
    private String naverTokenUri;
}

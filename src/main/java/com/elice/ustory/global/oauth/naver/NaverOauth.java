package com.elice.ustory.global.oauth.naver;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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

    public String getNaverToken(String code, String state){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naverClientId);
        params.add("client_secret", naverSecretKey);
        params.add("code", code);
        params.add("state", state);

        HttpEntity<MultiValueMap<String, String>> naverTokenRequest = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(naverTokenUri, HttpMethod.POST, naverTokenRequest, String.class);

        String responseBody = response.getBody();
        JsonObject asJsonObject = null;
        if(responseBody != null) asJsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
        return asJsonObject.get("access_token").getAsString();
    }
}

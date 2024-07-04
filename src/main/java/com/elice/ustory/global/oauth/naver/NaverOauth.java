package com.elice.ustory.global.oauth.naver;

import com.google.gson.JsonElement;
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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;

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

    @Value("${naver.tokenUri}")
    private String naverTokenUri;

    @Value("${naver.userInfo}")
    private String userInfoUri;

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

    public HashMap<String, Object> getUserInfoFromNaverToken(String accessToken){
        HashMap<String, Object> userInfo = new HashMap<>();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> naverUserInfoRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(userInfoUri, HttpMethod.POST, naverUserInfoRequest, String.class);
        log.info("response = {}", response);
        String responseBody = response.getBody();
        if(responseBody != null){
            responseBody = new String(responseBody.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        }
        JsonElement element = JsonParser.parseString(Objects.requireNonNull(responseBody));

        String id = element.getAsJsonObject().get("response").getAsJsonObject().get("id").getAsString();
        String nickname = element.getAsJsonObject().get("response").getAsJsonObject().get("nickname").getAsString();

        userInfo.put("id", id);
        userInfo.put("nickname", nickname);

        return userInfo;
    }
}

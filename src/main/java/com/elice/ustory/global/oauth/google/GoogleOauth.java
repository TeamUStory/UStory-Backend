package com.elice.ustory.global.oauth.google;

import com.elice.ustory.global.exception.model.NotFoundException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;

@Component
public class GoogleOauth {
    final String AUTHORIZATION_CODE = "authorization_code";
    final String ACCESS_TOKEN = "access_token";
    final String BEARER_TOKEN_PREFIX = "Bearer ";
    final String NOT_FOUND_ACCESS_TOKEN = "구글 액세스 토큰을 찾을 수 없습니다.";

    @Value("${google.clientId}")
    private String clientId;

    @Value("${google.clientSecret}")
    private String clientSecret;

    @Value("${google.redirectUri}")
    private String redirectUri;

    private String grantType = AUTHORIZATION_CODE;

    @Value("${google.accessTokenUri}")
    private String accessTokenUri;

    @Value("${google.accountProfileUri}")
    private String accountProfileUri;

    public String requestGoogleAccessToken(final String code) {
        RestTemplate restTemplate = new RestTemplate(); //TODO: 스프링 빈으로 관리
        final String decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8);
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", decodedCode);
        params.add("grant_type", grantType);
        params.add("redirect_uri", redirectUri);

        final HttpEntity<MultiValueMap<String, String>> tokenRequestEntity = new HttpEntity<>(params, headers);
        final String responseBody = restTemplate.exchange(accessTokenUri, HttpMethod.POST, tokenRequestEntity, String.class).getBody();
        JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();

        return Optional.ofNullable(responseJson.get(ACCESS_TOKEN))
                .map(JsonElement::getAsString)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ACCESS_TOKEN));
    }

    public HashMap<String, String> requestGoogleAccountProfile(String accessToken) {
        HashMap<String, String> accountProfile = new HashMap<>();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.AUTHORIZATION, BEARER_TOKEN_PREFIX + accessToken);
        final HttpEntity<MultiValueMap<String, String>> requestHeaderEntity = new HttpEntity<>(headers);
        final String responseBody = restTemplate.exchange(accountProfileUri, HttpMethod.GET, requestHeaderEntity, String.class).getBody();
        JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();

        String name = responseJson.get("name").getAsString();
        String email = responseJson.get("email").getAsString();

        accountProfile.put("name", name);
        accountProfile.put("email", email);

        return accountProfile;
    }
}

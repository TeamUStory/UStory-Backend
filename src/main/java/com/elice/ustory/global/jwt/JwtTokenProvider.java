package com.elice.ustory.global.jwt;

import com.elice.ustory.domain.user.entity.Users;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@Slf4j
@Getter
public class JwtTokenProvider {
    private final long ACCESSTOKEN_VALID_MILISECOND = 1000L * 60 * 30;
    private final long REFRESHTOKEN_VALID_MILISECOND = 1000L * 60 * 60 * 24 * 7;

    @Value("${key.salt}")
    private String salt;
    private Key secretKey;

    @PostConstruct
    protected void init() {
        log.info("[init] JwtTokenProvider 내 secretKey 초기화 시작");
        secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
        log.info("[init] JwtTokenProvider 내 SecretKey 초기화 완료");
    }

    public String createAccessToken(Long userId) {
        Claims claims = Jwts.claims();
        Date now = new Date();
        claims.put("userId", userId);
        claims.put("loginType", Users.LoginType.BASIC);
        log.info("[createAccessToken] access 토큰 생성 완료");
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis() + ACCESSTOKEN_VALID_MILISECOND))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createAccessTokenSocial(Long userId, String accessToken, Users.LoginType loginType) {
        Claims claims = Jwts.claims();
        Date now = new Date();
        claims.put("userId", userId);
        claims.put("socialToken", accessToken);
        claims.put("loginType", loginType);
        log.info("[createSocialAccessToken] access 토큰(소셜 로그인) 생성 완료");
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis() + ACCESSTOKEN_VALID_MILISECOND))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(){
        Claims claims = Jwts.claims();
        Date now = new Date();
        log.info("[createRefreshToken] refresh 토큰 생성 완료");
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis() + REFRESHTOKEN_VALID_MILISECOND))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}

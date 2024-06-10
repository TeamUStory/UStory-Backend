package com.elice.ustory.global.jwt;

import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.service.UserService;
import com.elice.ustory.global.exception.model.InvalidTokenException;
import com.elice.ustory.global.redis.kakao.KakaoToken;
import com.elice.ustory.global.redis.kakao.KakaoTokenService;
import com.elice.ustory.global.redis.refresh.RefreshToken;
import com.elice.ustory.global.redis.refresh.RefreshTokenService;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtUtil {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final KakaoTokenService kakaoTokenService;

    public boolean refreshAuthentication(HttpServletRequest request, HttpServletResponse response){
        String accessToken = getTokenFromRequest(request);
        RefreshToken refreshToken = refreshTokenService.getByAccessToken(accessToken)
                .orElseThrow();
        Users loginUser = userService.findById(Long.valueOf(refreshToken.getId()));

        log.info("redis안의 Refresh Token: {}", refreshToken.getRefreshToken());

        if (validateToken(refreshToken.getRefreshToken())) {
            log.info("[refreshToken] 기존 RefreshToken으로 AccessToken 재발급 && 새 RefreshToken 발급 시작");
            String newAccessToken = jwtTokenProvider.createAccessToken(Long.valueOf(refreshToken.getId()));
            String newRefreshToken = jwtTokenProvider.createRefreshToken();
            int remainingTTL = (int) getRemainingTTL(refreshToken.getRefreshToken());

            refreshTokenService.saveTokenInfo(loginUser.getId(), newRefreshToken, newAccessToken, remainingTTL);

            if(loginUser.getLoginType().toString().equals("KAKAO")){
                KakaoToken kakaoToken = kakaoTokenService.getByAccessToken(accessToken)
                        .orElseThrow();

                kakaoTokenService.saveKakaoTokenInfo(loginUser.getId(), kakaoToken.getKakaoToken(), newAccessToken);
            }
            log.info("[refreshToken] AccessToken이 재발급 되었습니다: {}", newAccessToken);
            log.info("[refreshToken] RefreshToken이 재발급 되었습니다: {}", newRefreshToken);

            response.addHeader("Authorization", "Bearer " + newAccessToken);
            return true;
        } else {
            log.warn("[refreshToken] RefreshToken이 만료 되었습니다.");
            return false;
        }
    }

    public String getTokenFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            log.info("이거 베어러 토큰임: {}", bearerToken);
            return bearerToken.substring("Bearer ".length());
        }else {
            throw new InvalidTokenException("토큰 형식이 잘못되었습니다.");
        }
    }

    public Long getUserPk(String token) {
        log.info("[getUserPk] 토큰 기반 회원 구별 정보 추출");
        return Long.parseLong(Jwts.parserBuilder().setSigningKey(jwtTokenProvider.getSecretKey()).build()
                .parseClaimsJws(token).getBody().get("userId").toString());
    }

    public boolean validateToken(String jwtToken) {
        log.info("[validateToken] 토큰 유효 체크 시작 ");
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(jwtTokenProvider.getSecretKey()).build()
                    .parseClaimsJws(jwtToken);

            return !claims.getBody().getExpiration().before(new Date(System.currentTimeMillis()));
        } catch (ExpiredJwtException e) {
            log.info("[validateToken] 토큰 유효 시간 만료");
            return false;
        } catch (SignatureException e){
            log.info("[validateToken] 올바르지 않은 토큰 형식");
            return false;
        }
    }

    public long getRemainingTTL(String jwtToken) {
        Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(jwtTokenProvider.getSecretKey()).build()
                .parseClaimsJws(jwtToken);
        Date expiration = claims.getBody().getExpiration();
        Date now = new Date();
        long remainingMillis = expiration.getTime() - now.getTime();
        return Math.max(remainingMillis, 0) / 1000;
    }

    public String getKakaoToken(String jwtToken){
        Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(jwtTokenProvider.getSecretKey()).build()
                .parseClaimsJws(jwtToken);
        return claims.getBody().get("kakao").toString();
    }
}

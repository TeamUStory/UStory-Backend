package com.elice.ustory.global.jwt;

import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.service.UserService;
import com.elice.ustory.global.exception.model.InvalidTokenException;
import com.elice.ustory.global.exception.model.RefreshTokenExpiredException;
import com.elice.ustory.global.redis.kakao.KakaoToken;
import com.elice.ustory.global.redis.kakao.KakaoTokenService;
import com.elice.ustory.global.redis.naver.NaverToken;
import com.elice.ustory.global.redis.naver.NaverTokenService;
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
    private final NaverTokenService naverTokenService;

    private static final String KAKAO_LOGIN_TYPE = "KAKAO";
    private static final String NAVER_LOGIN_TYPE = "NAVER";
    private static final String INVALID_TOKEN_MESSAGE = "토큰이 없거나 형식에 맞지 않습니다.";
    private static final String REFRESH_TOKEN_EXPIRED_MESSAGE = "RefreshToken이 만료되었습니다, 재로그인해주세요.";

    public String refreshAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getTokenFromRequest(request);
        RefreshToken refreshToken = refreshTokenService.getByAccessToken(accessToken)
                .orElseThrow((() -> new InvalidTokenException(INVALID_TOKEN_MESSAGE)));
        Users loginUser = userService.findById(Long.valueOf(refreshToken.getId()));

        log.info("redis안의 Refresh Token: {}", refreshToken.getRefreshToken());

        if (validateToken(refreshToken.getRefreshToken())) {
            log.info("[refreshToken] 기존 RefreshToken으로 AccessToken 재발급 && 새 RefreshToken 발급 시작");
            String newAccessToken = jwtTokenProvider.createAccessToken(Long.valueOf(refreshToken.getId()));
            String newRefreshToken = jwtTokenProvider.createRefreshToken();
            int remainingTTL = (int) getRemainingTTL(refreshToken.getRefreshToken());

            refreshTokenService.saveTokenInfo(loginUser.getId(), newRefreshToken, newAccessToken, remainingTTL);

            if (loginUser.getLoginType().toString().equals(KAKAO_LOGIN_TYPE)) {
                KakaoToken kakaoToken = kakaoTokenService.getByAccessToken(accessToken)
                        .orElseThrow(() -> new InvalidTokenException(INVALID_TOKEN_MESSAGE));

                kakaoTokenService.saveKakaoTokenInfo(loginUser.getId(), kakaoToken.getKakaoToken(), newAccessToken);
            } else if (loginUser.getLoginType().toString().equals(NAVER_LOGIN_TYPE)) {
                NaverToken naverToken = naverTokenService.getByAccessToken(accessToken)
                        .orElseThrow(() -> new InvalidTokenException(INVALID_TOKEN_MESSAGE));

                naverTokenService.saveNaverTokenInfo(loginUser.getId(), naverToken.getNaverToken(), newAccessToken);
            }
            log.info("[refreshToken] AccessToken이 재발급 되었습니다: {}", newAccessToken);
            log.info("[refreshToken] RefreshToken이 재발급 되었습니다: {}", newRefreshToken);

            return newAccessToken;
        } else {
            log.warn("[refreshToken] RefreshToken이 만료 되었습니다.");
            throw new RefreshTokenExpiredException(REFRESH_TOKEN_EXPIRED_MESSAGE);
        }
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            log.info("이거 베어러 토큰임: {}", bearerToken);
            return bearerToken.substring("Bearer ".length());
        } else {
            throw new InvalidTokenException(INVALID_TOKEN_MESSAGE);
        }
    }

    public Long getUserPk(String token) {
        log.info("[getUserPk] 토큰 기반 회원 구별 정보 추출");
        return Long.parseLong(Jwts.parserBuilder().setSigningKey(jwtTokenProvider.getSecretKey()).build()
                .parseClaimsJws(token).getBody().get("userId").toString());
    }

    public String getLoginType(String token) {
        log.info("[getLoginType] 현재 로그인 된 유저의 로그인 방식 추출");
        return Jwts.parserBuilder().setSigningKey(jwtTokenProvider.getSecretKey()).build()
                .parseClaimsJws(token).getBody().get("loginType").toString();
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
        } catch (InvalidTokenException e) {
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

    public String getSocialToken(String jwtToken) {
        Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(jwtTokenProvider.getSecretKey()).build()
                .parseClaimsJws(jwtToken);
        return claims.getBody().get("socialToken").toString();
    }
}

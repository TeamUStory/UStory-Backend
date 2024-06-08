package com.elice.ustory.global.jwt;

import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.service.UserService;
import com.elice.ustory.global.redis.kakao.KakaoToken;
import com.elice.ustory.global.redis.kakao.KakaoTokenService;
import com.elice.ustory.global.redis.refresh.RefreshToken;
import com.elice.ustory.global.redis.refresh.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

        if (jwtTokenProvider.validateToken(refreshToken.getRefreshToken())) {
            log.info("[refreshToken] 기존 RefreshToken으로 AccessToken 재발급 && 새 RefreshToken 발급 시작");
            String newAccessToken = jwtTokenProvider.createAccessToken(Long.valueOf(refreshToken.getId()));
            String newRefreshToken = jwtTokenProvider.createRefreshToken();
            int remainingTTL = (int) jwtTokenProvider.getRemainingTTL(refreshToken.getRefreshToken());

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
        }

        return null;
    }
}

package com.elice.ustory.global.jwt;

import com.elice.ustory.global.exception.model.ForbiddenException;
import com.elice.ustory.global.redis.refresh.RefreshToken;
import com.elice.ustory.global.redis.refresh.RefreshTokenRepository;
import com.elice.ustory.global.redis.refresh.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtUtil {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;

    public boolean refreshAuthentication(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        String accessToken = getTokenFromRequest(request, "Authorization"); // TODO: Redis RefreshToken에 맞게 수정 1
        RefreshToken refreshToken = refreshTokenRepository.findByAccessToken(accessToken)
                .orElseThrow();


        log.info("redis안의 토큰: {}", refreshToken.getRefreshToken());
        if (jwtTokenProvider.validateToken(refreshToken.getRefreshToken())) {
            log.info("[refreshToken] 기존 RefreshToken으로 AccessToken 재발급 && 새 RefreshToken 발급 시작");
            String newAccessToken = jwtTokenProvider.createAccessToken(Long.valueOf(refreshToken.getId()));
            String newRefreshToken = jwtTokenProvider.createRefreshToken();
            int remainingTTL = (int) jwtTokenProvider.getRemainingTTL(refreshToken.getRefreshToken());

            refreshTokenService.saveTokenInfo(Long.valueOf(refreshToken.getId()), newRefreshToken, newAccessToken, remainingTTL);
            Cookie cookie = new Cookie("Authorization", URLEncoder.encode("Bearer " + newAccessToken, StandardCharsets.UTF_8));
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);
            response.addCookie(cookie);
            log.info("[refreshToken] AccessToken이 재발급 되었습니다.");
            return true;
        } else {
            log.warn("[refreshToken] RefreshToken이 만료 되었습니다.");
            return false;
        }
    } // TODO: Redis RefreshToken에 맞게 수정 2

    public String getTokenFromRequest(HttpServletRequest request, String tokenName) throws UnsupportedEncodingException {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String accessToken = bearerToken.substring("Bearer ".length());
            return accessToken;
        }

        throw new ForbiddenException("유효하지 않은 토큰입니다.");

//        if (request.getCookies() != null) {
//            Optional<Cookie> tokenCookie = Arrays.stream(request.getCookies())
//                    .filter(
//                            cookie -> cookie.getName().equals(tokenName)
//                    ).findFirst();
//
//            if (tokenCookie.isPresent()) {
//                String token = URLDecoder.decode(tokenCookie.get().getValue(), "UTF-8");
//                if (token != null && token.startsWith("Bearer ")) {
//                    return token.substring(7);
//                }
//                log.info(token);
//            }
//        }
//        return null;
    }
}

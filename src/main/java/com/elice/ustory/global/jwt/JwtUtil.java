package com.elice.ustory.global.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtUtil {
    private final JwtTokenProvider jwtTokenProvider;

    public boolean refreshAuthentication(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        String refreshToken = getTokenFromRequest(request, "Refresh"); // TODO: Redis RefreshToken에 맞게 수정 1
        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            log.info("[refreshToken] RefreshToken으로 AccessToken 재발급 시작");
            String accessToken = jwtTokenProvider.createAccessToken(jwtTokenProvider.getUserPk(refreshToken));
            Cookie cookie = new Cookie("Authorization", accessToken);
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
        if (request.getCookies() != null) {
            Optional<Cookie> tokenCookie = Arrays.stream(request.getCookies())
                    .filter(
                            cookie -> cookie.getName().equals(tokenName)
                    ).findFirst();

            if (tokenCookie.isPresent()) {
                String token = URLDecoder.decode(tokenCookie.get().getValue(), "UTF-8");
                if (token != null && token.startsWith("Bearer ")) {
                    return token.substring(7);
                }
                log.info(token);
            }
        }
        return null;
    }
}

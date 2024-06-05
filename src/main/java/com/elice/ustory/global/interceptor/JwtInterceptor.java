package com.elice.ustory.global.interceptor;

import com.elice.ustory.global.jwt.JwtAuthorization;
import com.elice.ustory.global.jwt.JwtTokenProvider;
import com.elice.ustory.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Parameter;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtUtil jwtUtil;

    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler)
            throws Exception {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        if (handler instanceof HandlerMethod handlerMethod) {
            boolean requiresAuthorization = false;

            for (Parameter parameter : handlerMethod.getMethod().getParameters()) {
                if (parameter.isAnnotationPresent(JwtAuthorization.class)) {
                    JwtAuthorization jwtAuthorization = parameter.getAnnotation(JwtAuthorization.class);
                    requiresAuthorization = jwtAuthorization.required();
                    break;
                }
            }

            if (requiresAuthorization) {
                String accessToken = jwtUtil.getTokenFromRequest(request, "Authorization");

                log.info("[preHandle] accessToken 값 추출 완료, token: {}", accessToken);
                log.info("[preHandle] accessToken 값 유효성 체크 시작");

                if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
                    log.info("[preHandle] accessToken 값 유효성 체크 완료");
                    return HandlerInterceptor.super.preHandle(request, response, handler);
                } else {
                    log.warn("[preHandle] AccessToken이 만료되었습니다.");
                    return jwtUtil.refreshAuthentication(request, response);
                }
            }
        }
        return true;
    }
}

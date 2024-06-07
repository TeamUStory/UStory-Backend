package com.elice.ustory.global.resolver;

import com.elice.ustory.global.jwt.JwtAuthorization;
import com.elice.ustory.global.jwt.JwtTokenProvider;
import com.elice.ustory.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtUtil jwtUtil;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JwtAuthorization.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory){

        log.info("JwtAuthorizationArgumentResolver 동작!!");

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);


        String accessToken = jwtUtil.getTokenFromRequest(request);
        if (accessToken != null) {
            if (jwtTokenProvider.validateToken(accessToken)) {
                return jwtTokenProvider.getUserPk(accessToken);
            }

            // 토큰은 없지만 필수가 아닌 경우 체크
            JwtAuthorization annotation = parameter.getParameterAnnotation(JwtAuthorization.class);
            if (annotation != null && !annotation.required()) {
                // 필수가 아닌 경우 기본 객체 리턴
                return jwtTokenProvider.getUserPk(accessToken);
            }
        }

        // 토큰 값이 없으면 에러
        throw new RuntimeException("권한 없음.");
    }

}

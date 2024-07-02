package com.elice.ustory.global.resolver;

import com.elice.ustory.global.exception.model.InvalidTokenException;
import com.elice.ustory.global.jwt.JwtAuthorization;
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
            if (jwtUtil.validateToken(accessToken)) {
                return jwtUtil.getUserPk(accessToken);
            }

            JwtAuthorization annotation = parameter.getParameterAnnotation(JwtAuthorization.class);
            if (annotation != null && !annotation.required()) {
                return jwtUtil.getUserPk(accessToken);
            }
        }

        throw new InvalidTokenException("토큰 형식이 잘못되었습니다.");
    }

}

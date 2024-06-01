//package com.elice.ustory.global.config;
//
//import com.elice.ustory.global.interceptor.JwtInterceptor;
//import com.elice.ustory.global.jwt.JwtTokenProvider;
//import com.elice.ustory.global.jwt.JwtUtil;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//@RequiredArgsConstructor
//public class WebMvcConfig implements WebMvcConfigurer {
//    private final JwtTokenProvider jwtTokenProvider;
//    private final JwtUtil jwtUtil;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new JwtInterceptor(jwtTokenProvider, jwtUtil))
//                .addPathPatterns("/**");
//        //TODO: 로그인 관련 엔드포인트는 제외시켜야함
//    }
//}

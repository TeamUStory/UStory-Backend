//package com.elice.ustory.global.config;
//
//import com.elice.ustory.global.interceptor.JwtInterceptor;
//import com.elice.ustory.global.jwt.JwtTokenProvider;
//import com.elice.ustory.global.jwt.JwtUtil;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//
//@Configuration
//@RequiredArgsConstructor
//public class WebMvcConfig extends WebMvcConfigurationSupport {
//    private final JwtTokenProvider jwtTokenProvider;
//    private final JwtUtil jwtUtil;
//
//    protected void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new JwtInterceptor(jwtTokenProvider, jwtUtil))
//                .addPathPatterns("/**");
//    }
//}

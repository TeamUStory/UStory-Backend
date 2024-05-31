package com.elice.ustory.global.config;

import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.service.UserService;
import com.elice.ustory.global.filter.JwtTokenFilter;
import com.elice.ustory.global.jwt.JwtTokenProvider;
import com.elice.ustory.global.oauth.kakao.KakaoOauth;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Slf4j
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoOauth kakaoOauth;
    private final UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 무상태성
        http.setSharedObject(SessionManagementConfigurer.class,
                new SessionManagementConfigurer<HttpSecurity>().sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // csrf, http basic 등 비활성화
        http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JwtTokenFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

//        http.logout((logout) -> logout.logoutUrl("로그아웃 api url")
//                .addLogoutHandler((request, response, auth) -> {
//                    String kakaoAccessToken;
//                    Users user = userService.readByNickname(auth.getName());
//                    if(user.getLoginType().toString().equals("KAKAO")){
//                        if (request.getCookies() != null) {
//                            Optional<Cookie> tokenCookie = Arrays.stream(request.getCookies())
//                                    .filter(
//                                            cookie -> cookie.getName().equals("Authorization")
//                                    ).findFirst();
//
//                            String token;
//
//                            try {
//                                token = URLDecoder.decode(tokenCookie.get().getValue(), "UTF-8");
//                            } catch (UnsupportedEncodingException e) {
//                                throw new RuntimeException(e);
//                            }
//
//                            if (token != null && token.startsWith("Bearer ")) {
//                                kakaoAccessToken = token.substring(7);
//                                kakaoOauth.expireKakaoToken(kakaoAccessToken);
//                                log.info("[kakao] Kakao 토큰 비활성화 완료");
//                            }
//                        }
//                    }
//
//                    for (Cookie cookie : request.getCookies()) {
//                        String cookieName = cookie.getName();
//                        Cookie cookieToDelete = new Cookie(cookieName, null);
//                        cookieToDelete.setMaxAge(0);
//                        cookieToDelete.setPath("/");
//                        response.addCookie(cookieToDelete);
//                    }
//                })
//                .logoutSuccessUrl("로그인 창 url")
//        );
        return http.build();
    }
}

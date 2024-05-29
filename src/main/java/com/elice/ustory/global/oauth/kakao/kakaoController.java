package com.elice.ustory.global.oauth.kakao;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@Slf4j
@RequestMapping
@RequiredArgsConstructor
public class kakaoController {
    private final KakaoOauth kakaoOauth;

    @GetMapping("/login")
    public String showLogInForm(Model model) {
        model.addAttribute("kakaoApiKey", kakaoOauth.getKakaoApiKey());
        model.addAttribute("redirectUri", kakaoOauth.getKakaoRedirectUri());
        return "login/login";
    }

    @RequestMapping("${kakao.redirect_shortUri}")
    public void kakaoLogin(@RequestParam String code, HttpServletResponse response) {
        String accessToken = kakaoOauth.getKakaoAccessToken(code, response);
        Map<String, Object> userInfo = kakaoOauth.getUserInfoFromKakaoToken(accessToken);

        String id = (String) userInfo.get("id");
        String nickname = (String) userInfo.get("nickname");

        log.info("[kakaoLogin] 카카오 닉네임: {}", nickname);
        log.info("[kakaoLogin] 카카오 로그인 액세스 토큰: {}", accessToken);
    }
}

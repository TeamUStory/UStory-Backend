package com.elice.ustory.global.oauth.kakao;

import com.elice.ustory.domain.user.dto.LoginResponse;
import com.elice.ustory.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
public class KakaoController {
    private final KakaoOauth kakaoOauth;
    private final UserService userService;
    private final KakaoService kakaoService;

    @GetMapping("/login")
    public String showLogInForm(Model model) {
        model.addAttribute("kakaoApiKey", kakaoOauth.getKakaoApiKey());
        model.addAttribute("redirectUri", kakaoOauth.getKakaoLoginRedirectUri());
        return "login/login";
    }

    @RequestMapping("/login/oauth2/code/kakao")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam String code, HttpServletResponse response) {
        String accessToken = kakaoOauth.getKakaoAccessToken(code, response);
        Map<String, Object> userInfo = kakaoOauth.getUserInfoFromKakaoToken(accessToken);

        String id = (String) userInfo.get("id");
        String nickname = (String) userInfo.get("nickname");

        if(!userService.checkByEmail(id+"@ustory.com")){
            kakaoService.kakaoSignUp(id, nickname);
        }

        LoginResponse loginResponse = kakaoService.kakaoLogin(id, response);

        log.info("[kakaoLogin] 카카오 닉네임: {}", nickname);
        return ResponseEntity.ok().body(loginResponse);
    }
}
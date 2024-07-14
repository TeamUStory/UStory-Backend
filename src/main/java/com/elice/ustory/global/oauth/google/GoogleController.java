package com.elice.ustory.global.oauth.google;

import com.elice.ustory.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

@Controller
@Slf4j
@RequiredArgsConstructor
public class GoogleController {
    private final GoogleOauth googleOauth;
    private final GoogleService googleService;
    private final UserService userService;

    @RequestMapping(value = "/login/oauth2/code/google", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<HashMap<String, String>> googleLogin(@RequestParam(name = "code") String code) {
        String accessToken = googleOauth.requestGoogleAccessToken(code);
        HashMap<String, String> accountProfile = googleOauth.requestGoogleAccountProfile(accessToken);

        String email = accountProfile.get("email");
        String name = accountProfile.get("name");

        if(!userService.checkExistByEmail(email)) {
            googleService.googleSignUp(accountProfile);
        }
        //TODO: 이미 구글 이메일로 기본 회원가입을 했는데, 소셜로그인을 시도할 경우? -> "이미 가입된 이메일입니다. 다른 로그인 방식을 시도해보세요."

        log.info("[googleLogin] 구글 닉네임: {}", name);
        return ResponseEntity.ok().body(accountProfile);
    }
}

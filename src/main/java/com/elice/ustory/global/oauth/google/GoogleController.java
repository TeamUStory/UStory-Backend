package com.elice.ustory.global.oauth.google;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class GoogleController {
    private final GoogleOauth googleOauth;

    @RequestMapping(value = "/login/oauth2/code/google", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> googleLogin(@RequestParam(name = "code") String code) {
        String accessToken = googleOauth.requestGoogleAccessToken(code);

        return ResponseEntity.ok().body(accessToken);
    }
}

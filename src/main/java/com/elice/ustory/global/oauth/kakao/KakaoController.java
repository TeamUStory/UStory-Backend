package com.elice.ustory.global.oauth.kakao;

import com.elice.ustory.domain.user.dto.LoginResponse;
import com.elice.ustory.domain.user.dto.LogoutResponse;
import com.elice.ustory.domain.user.dto.auth.ChangePwdVerifyResponse;
import com.elice.ustory.domain.user.service.UserService;
import com.elice.ustory.global.exception.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Tag(name = "KAKAO", description = "KAKAO OAUTH API")
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
        model.addAttribute("redirectLogoutUri", kakaoOauth.getKakaoLogoutRedirectUri());
        return "login/login";
    }

    @Operation(summary = "KAKAO LOGIN API", description = "카카오 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(value = "/login/oauth2/code/kakao", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam String code, HttpServletResponse response) {
        String kakaoAccessToken = kakaoOauth.getKakaoAccessToken(code);
        Map<String, Object> userInfo = kakaoOauth.getUserInfoFromKakaoToken(kakaoAccessToken);

        String id = (String) userInfo.get("id");
        String nickname = (String) userInfo.get("nickname");

        if(!userService.checkExistByEmail(id + "@ustory.com")){
            kakaoService.kakaoSignUp(id, nickname);
        }

        LoginResponse loginResponse = kakaoService.kakaoLogin(id, response, kakaoAccessToken);

        log.info("[kakaoLogin] 카카오 닉네임: {}", nickname);
        return ResponseEntity.ok().body(loginResponse);
    }

    @Operation(summary = "KAKAO LOGOUT API", description = "카카오 로그아웃")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LogoutResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(value = "/auth/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<LogoutResponse> KakaoLogout(HttpServletRequest request) {
        LogoutResponse logoutResponse = kakaoService.kakaoLogout(request);
        return ResponseEntity.ok().body(logoutResponse);
    }
}

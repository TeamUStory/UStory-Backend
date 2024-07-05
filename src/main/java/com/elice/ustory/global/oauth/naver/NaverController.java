package com.elice.ustory.global.oauth.naver;

import com.elice.ustory.domain.user.dto.LoginResponse;
import com.elice.ustory.domain.user.dto.LogoutResponse;
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

import java.util.HashMap;

@Tag(name = "NAVER", description = "NAVER OAUTH API")
@Controller
@Slf4j
@RequestMapping
@RequiredArgsConstructor
public class NaverController {
    private final NaverOauth naverOauth;
    private final NaverService naverService;
    private final UserService userService;

    @Operation(summary = "NAVER LOGIN API", description = "네이버 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })

    @GetMapping("login")
    public String login(Model model){
        model.addAttribute("naverClientId", naverOauth.getNaverClientId());
        model.addAttribute("redirectUri", naverOauth.getNaverLoginRedirectUri());
        model.addAttribute("redirectLogoutUri", naverOauth.getNaverLogoutRedirectUri());
        model.addAttribute("state", "STATE_STRING");
        return "login/login";
    }

    @RequestMapping(value = "/login/oauth2/code/naver", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<LoginResponse> naverLogin(@RequestParam(name = "code") String code,
                                                    @RequestParam(name = "state") String state,
                                                    HttpServletResponse response) {
        String naverAccessToken = naverOauth.getNaverToken(code, state);
        HashMap<String, Object> userInfo = naverOauth.getUserInfoFromNaverToken(naverAccessToken);

        String nickname = (String) userInfo.get("nickname");
        String naverEmail = (String) userInfo.get("email");

        if(!userService.checkExistByEmail(naverEmail)){
            naverService.naverSignUp(nickname, naverEmail);
        }

        LoginResponse loginResponse = naverService.naverLogin(naverEmail, response, naverAccessToken);

        log.info("[naverLogin] 네이버 닉네임: {}", nickname);
        return ResponseEntity.ok().body(loginResponse);
    }

    @Operation(summary = "NAVER LOGOUT API", description = "네이버 로그아웃")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LogoutResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(value = "/auth/logout/naver", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<LogoutResponse> naverLogout(HttpServletRequest request) {
        LogoutResponse logoutResponse = naverService.naverLogout(request);
        return ResponseEntity.ok().body(logoutResponse);
    }
}

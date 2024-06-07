package com.elice.ustory.global.jwt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "JWT", description = "JWT TOKEN API")
@RestController
@RequestMapping("/jwt")
@RequiredArgsConstructor
@Slf4j
public class JwtController {

    private final JwtUtil jwtUtil;

    @Operation(summary = "Reissue Access Token API", description = "토큰 만료 시 새로운 Access Token 발급")
    @PostMapping("/re-issue")
    public ResponseEntity<String> reIssueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        log.info("[reIssueAccessToken API] AccessToken 재발급 API 시작");

        boolean isRefreshed = jwtUtil.refreshAuthentication(request, response);

        if (isRefreshed) {
            log.info("[handleAccessTokenExpiredException] AccessToken 갱신 완료");
            return ResponseEntity.ok().body(response.getHeader("Authorization"));
        } else {
            log.warn("[handleAccessTokenExpiredException] RefreshToken이 만료되었습니다. 재로그인 필요.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("RefreshToken이 만료되었습니다. 다시 로그인해주세요.");
        }
    }
}

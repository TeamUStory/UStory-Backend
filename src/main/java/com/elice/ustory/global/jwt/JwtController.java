package com.elice.ustory.global.jwt;

import com.elice.ustory.global.exception.dto.ErrorResponse;
import com.elice.ustory.global.exception.model.RefreshTokenExpiredException;
import com.elice.ustory.global.s3.dto.presignedUrlResponse;
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = presignedUrlResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/re-issue")
    public ResponseEntity<JwtRefreshResponse> reIssueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        log.info("[reIssueAccessToken API] AccessToken 재발급 API 시작");

        String refreshedToken = jwtUtil.refreshAuthentication(request, response);

        if (refreshedToken == null) {
            log.warn("[handleAccessTokenExpiredException] RefreshToken이 만료되었습니다. 재로그인 필요.");
            throw new RefreshTokenExpiredException("RefreshToken이 만료되었습니다, 재로그인해주세요.");
        }

        log.info("[handleAccessTokenExpiredException] AccessToken 갱신 완료");
        return ResponseEntity.ok().body(new JwtRefreshResponse(refreshedToken));
    }
}

package com.elice.ustory.domain.user.controller;

import com.elice.ustory.domain.user.dto.FindByNicknameResponse;
import com.elice.ustory.domain.user.dto.*;
import com.elice.ustory.domain.user.dto.auth.*;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.service.EmailService;
import com.elice.ustory.domain.user.service.UserService;
import com.elice.ustory.global.exception.dto.ErrorResponse;
import com.elice.ustory.global.jwt.JwtAuthorization;
import com.elice.ustory.global.jwt.JwtUtil;
import com.elice.ustory.global.oauth.kakao.KakaoService;
import com.elice.ustory.global.oauth.naver.NaverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final KakaoService kakaoService;
    private final NaverService naverService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Create User API", description = "기본 회원가입 후 유저를 생성한다." +
            "<br>비밀번호는 **숫자, 영문, 특수문자 각 1개를 포함한 8~16자** 이며," +
            "<br>보안을 위해, 이때 특수문자는 **~!@#%^*** 만 허용한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Users.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/sign-up")
    public ResponseEntity<Users> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        // TODO: 이메일 인증 등의 절차가 모두 완료되었는지 확인 후 회원가입이 진행되어야 함
        Users newUser = userService.signUp(signUpRequest);
        return ResponseEntity.ok().body(newUser);
    }

    @Operation(summary = "Update User API", description = "로그인한 회원의 정보를 수정한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Users.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping
    public ResponseEntity<Users> updateCurrentUser(@JwtAuthorization Long userId,
                                                   @Valid @RequestBody UpdateRequest updateRequest) {
        Users updatedUser = userService.updateUser(updateRequest, userId);
        return ResponseEntity.ok().body(updatedUser);
    }

    @Operation(summary = "Delete User API", description = "로그인한 회원을 삭제한다. 이메일과 닉네임 재사용 불가. (소프트 딜리트)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Users.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping
    public ResponseEntity<Users> deleteCurrentUser(@JwtAuthorization Long userId) {
        Users deletedUser = userService.deleteUser(userId);
        return ResponseEntity.ok().body(deletedUser);
    }

    @Operation(summary = "User Login API", description = "아이디와 비밀번호로 로그인한다. 성공 시 토큰 반환, 실패 시 null 반환.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginBasic(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse loginResponse = userService.login(loginRequest, response);
        return ResponseEntity.ok().body(loginResponse);
    }

    @Operation(summary = "User Logout API", description = "현재 유저를 로그아웃한다: 쿠키 만료, 리프레시 토큰 삭제.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LogoutResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logoutBasic(HttpServletRequest request) {
        String accessToken = jwtUtil.getTokenFromRequest(request);
        String loginType = jwtUtil.getLoginType(accessToken);

        if(loginType.equals("KAKAO")){
            kakaoService.kakaoLogout(accessToken);
        }else if(loginType.equals("NAVER")){
            naverService.naverLogout(accessToken);
        }

        LogoutResponse logoutResponse = userService.logout(accessToken);
        return ResponseEntity.ok().body(logoutResponse);
    }

    @Operation(summary = "Search User by Nickname API", description = "닉네임이 일치하는 사용자를 검색합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FindByNicknameResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<FindByNicknameResponse> searchUserByNickname(@RequestParam(name = "nickname") String nickname) {
        FindByNicknameResponse findByNicknameResponse = userService.searchUserByNickname(nickname);
        return ResponseEntity.ok(findByNicknameResponse);
    }


    @Operation(summary = "User MyPage API", description = "마이페이지에 필요한 정보를 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MyPageResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/my-page")
    public ResponseEntity<MyPageResponse> showMyPage(@JwtAuthorization Long userId) {
        MyPageResponse myPageResponse = userService.showMyPage(userId);
        return ResponseEntity.ok(myPageResponse);
    }

    @Operation(summary = "Validate Nickname API", description = "회원가입 및 회원정보 수정 시, 닉네임 중복 여부를 검증한다. (글자 수 등 조건은 삭제됨)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidateNicknameResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/validate-nickname")
    public ResponseEntity<ValidateNicknameResponse> validateNickname(@Valid @RequestBody ValidateNicknameRequest validateNicknameRequest) {
        ValidateNicknameResponse validateNicknameResponse = userService.isValidNickname(validateNicknameRequest);
        return ResponseEntity.ok(validateNicknameResponse);
    }

    @Operation(summary = "Send Mail To Validate Email For Sign-Up API",
            description = "회원가입 시 이메일 검증을 위한 인증코드를 해당 메일로 발송한다. 이미 가입된 이메일인 경우 예외 발생." +
            "<br>detailMessage는 둘 중 하나: '사용중인\\_이메일' 또는 '탈퇴된\\_이메일'")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthCodeCreateResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/sign-up/send-validate")
    public ResponseEntity<AuthCodeCreateResponse> SendMailToValidateForSignUp(@Valid @RequestBody AuthCodeCreateRequest authCodeCreateRequest) throws MessagingException {
        AuthCodeCreateResponse authCodeCreateResponse = emailService.sendValidateSignupMail(authCodeCreateRequest.getEmail());
        return ResponseEntity.ok(authCodeCreateResponse);
    }

    @Operation(summary = "Verify Validate Code For Sign-Up API", description = "사용자가 입력한 회원가입 인증코드의 유효성을 검증한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthCodeVerifyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/sign-up/verify-validate")
    public ResponseEntity<AuthCodeVerifyResponse> verifyAuthCodeForSignUp(@Valid @RequestBody AuthCodeVerifyRequest authCodeVerifyRequest) {
        AuthCodeVerifyResponse authCodeVerifyResponse = emailService.verifySignupAuthCode(authCodeVerifyRequest);
        return ResponseEntity.ok(authCodeVerifyResponse);
    }

    @Operation(summary = "Send Mail To Validate User For Change-Password API", description = "로그인한 사용자가 비밀번호 변경을 위해, 이메일 인증 코드 발송을 요청한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChangePwdCallResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/change-password/send-validate")
    public ResponseEntity<ChangePwdCallResponse> sendMailToValidateForChangePwd(@Valid @RequestBody ChangePwdCallRequest changePwdCallRequest) throws MessagingException {
        ChangePwdCallResponse changePwdCallResponse = emailService.sendValidateUserMailForPwd(changePwdCallRequest);
        return ResponseEntity.ok(changePwdCallResponse);
    }

    @Operation(summary = "Verify Validate Code For Change-Password API", description = "비밀번호 변경을 위해 입력된 인증 코드를 검증한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChangePwdVerifyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/change-password/verify-validate")
    public ResponseEntity<ChangePwdVerifyResponse> verifyAuthCodeForChangePwd(@Valid @RequestBody ChangePwdVerifyRequest changePwdVerifyRequest) {
        ChangePwdVerifyResponse changePwdVerifyResponse = emailService.verifyChangePwdCode(changePwdVerifyRequest);
        return ResponseEntity.ok(changePwdVerifyResponse);
    }

    @Operation(summary = "Change Password For Current-User API", description = "비밀번호를 잊어버린 유저의 비밀번호를 재설정한다. 인증 이메일 발송을 거친 후 임시 토큰과 함께 호출되어야 한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChangePwdVerifyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/change-password")
    public ResponseEntity changeLostPassword(@JwtAuthorization Long userId, @Valid @RequestBody ChangePwdRequest changePwdRequest) {
        userService.updateLostPassword(userId, changePwdRequest);
        return ResponseEntity.ok().body("비밀번호가 재설정되었습니다.");
    }
}

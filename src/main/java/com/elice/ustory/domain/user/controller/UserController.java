package com.elice.ustory.domain.user.controller;

import com.elice.ustory.domain.user.dto.*;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create User API", description = "기본 회원가입 후 유저를 생성한다.")
    @PostMapping
    public ResponseEntity<Users> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        Users newUser = userService.signUp(signUpRequest);
        return ResponseEntity.ok().body(newUser);
    }

    @Operation(summary = "Update User API", description = "로그인한 회원의 정보를 수정한다.")
    @PutMapping
    public ResponseEntity<Users> updateCurrentUser(@Valid @RequestBody UpdateRequest updateRequest) {
        Users updatedUser = userService.updateUser(updateRequest);
        return ResponseEntity.ok().body(updatedUser);
    }

    @Operation(summary = "Delete User API", description = "로그인한 회원을 삭제한다. (소프트 딜리트)")
    @DeleteMapping
    public ResponseEntity<Users> deleteCurrentUser(@Valid @RequestBody DeleteRequest deleteRequest) {
        Users deletedUser = userService.deleteUser(deleteRequest);
        return ResponseEntity.ok().body(deletedUser);
    }

    @Operation(summary = "User Login API", description = "아이디와 비밀번호로 로그인한다.")
    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> loginBasic(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        String id = loginRequest.getLoginEmail();
        String password = loginRequest.getPassword();
        LoginResponse loginResponse = userService.login(id, password, response);
        log.info("[logIn] 정상적으로 로그인되었습니다. id : {}, token : {}", id, loginResponse.getAccessToken());

        //TODO: 유저 정보 Cookie에 저장
        return ResponseEntity.ok().body(loginResponse);
    }

    @Operation(summary = "User MyPage API", description = "마이페이지에 필요한 정보를 조회한다.")
    @GetMapping(value = "/my-page")
    public ResponseEntity<MyPageResponse> showMyPage(@Valid @RequestParam("userId") Long userId) {
        MyPageResponse myPageResponse = userService.showMyPage(userId);
        return ResponseEntity.ok(myPageResponse);
    }

    @Operation(summary = "Validate Nickname", description = "회원가입 및 회원정보 수정 시, 중복 또는 글자 수 등, 닉네임 유효 여부를 검증한다.")
    @PostMapping(value = "/validate-nickname")
    public ResponseEntity<ValidateNicknameResponse> validateNickname(@Valid @RequestBody ValidateNicknameRequest validateNicknameRequest) {
        ValidateNicknameResponse validateNicknameResponse = userService.isValid(validateNicknameRequest);
        return ResponseEntity.ok(validateNicknameResponse);
    }
//    @GetMapping("/get-member")
//    public Long getCurrentMember(Authentication authentication){
//        log.info("authentication.getName() : {}", authentication.getName());
//        Users user = userService.readByNickname(authentication.getName());
//        return user.getId();
//    }
}

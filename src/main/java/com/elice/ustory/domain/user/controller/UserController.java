package com.elice.ustory.domain.user.controller;

import com.elice.ustory.domain.user.dto.*;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create User API", description = "기본 회원가입 후 유저를 생성한다.")
    @PostMapping
    public ResponseEntity<Users> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        Users newUser = userService.signUp(signUpRequest);
        return ResponseEntity.ok().body(newUser);
    }

    @Operation(summary = "Read User API", description = "닉네임이 일치하는 유저를 조회한다.")
    @GetMapping
    public ResponseEntity<Users> readByNickname(@Parameter @Valid @RequestParam("nickname") String nickname) {
        Users user = userService.readByNickname(nickname);
        return ResponseEntity.ok().body(user);
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
    public ResponseEntity<LoginResponse> loginBasic(@Valid @RequestBody LoginRequest loginRequest) {
        String id = loginRequest.getLoginEmail();
        String password = loginRequest.getPassword();
        LoginResponse loginResponse = userService.login(id, password);

        //TODO: 유저 정보 Cookie에 저장
        return ResponseEntity.ok().body(loginResponse);
    }
}

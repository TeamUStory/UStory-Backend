package com.elice.ustory.domain.user.service;

import com.elice.ustory.domain.user.dto.*;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.jwt.JwtTokenProvider;
//import com.elice.ustory.global.redis.refresh.RefreshToken;
//import com.elice.ustory.global.redis.refresh.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
//    private final RefreshTokenRepository refreshTokenRepository;

    public Optional<Users> findById(Long userId){
        return userRepository.findById(userId);
    }

    public Users signUp(SignUpRequest signUpRequest) {

        String email = signUpRequest.getEmail();
        Users.LoginType loginType = Users.LoginType.BASIC;
        String name = signUpRequest.getName();
        String nickname = signUpRequest.getNickname();
        String password = signUpRequest.getPassword();
        String profileImg = signUpRequest.getProfileImg();

        Users builtUser = Users.addUserBuilder()
                .email(email)
                .loginType(loginType)
                .name(name)
                .nickname(nickname)
                .password(password)
                .profileImg(profileImg)
                .build();

        Users newUser = userRepository.save(builtUser);
        return newUser;
    }

    public Users readByNickname(String nickname) {

        //TODO: Optional 예외처리
        Users foundUser = userRepository.findByNickname(nickname).orElseThrow(() -> new IllegalArgumentException("no such data"));

        return foundUser;
    }

    public Users updateUser(UpdateRequest updateRequest) {
        //TODO: 회원 정보 수정 시 Access Token 재발급 해야함
        //TODO: Optional 예외처리
        Users user = userRepository
                .findById(updateRequest.getUserId())
                .orElseThrow();

        String name = updateRequest.getName();
        String nickname = updateRequest.getNickname();
        String password = updateRequest.getPassword();
        String profileImg = updateRequest.getProfileImg();

        if(name != null) {
            user.setName(name);
        }
        if(nickname != null) {
            user.setNickname(nickname);
        }
        if(password != null) {
            user.setPassword(password);
        }
        if(profileImg != null) {
            user.setProfileImg(profileImg);
        }

        Users updatedUser = userRepository.save(user);
        return updatedUser;
    }

    public Users deleteUser(DeleteRequest deleteRequest) {

        Long userId = deleteRequest.getUserId();

        //TODO: 예외처리
        Users user = userRepository.findById(userId)
                .orElseThrow();

        user.setDeletedAt(LocalDateTime.now());

        Users deletedUser = userRepository.save(user);
        return deletedUser;
    }

    public LoginResponse login(String id, String password, HttpServletResponse response) {
        //TODO: 예외처리
        Users loginUser = userRepository.findByEmail(id)
                .orElseThrow();
        log.info("[getSignInResult] Id : {}", id);

        if (!loginUser.getPassword().equals(password)) {
            //TODO: 비밀번호 오류 예외처리
            return null;
        }

        log.info("[getLogInResult] 패스워드 일치");
        log.info("[getLogInResult] LogInResponse 객체 생성");
        String accessToken = jwtTokenProvider.createAccessToken(
                loginUser.getNickname()
        );

        String refreshToken = jwtTokenProvider.createRefreshToken();

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();


        log.info("[getLogInResult] LogInResponse 객체에 값 주입");
        var cookie1 = new Cookie("Authorization", URLEncoder.encode("Bearer " + loginResponse.getAccessToken(), StandardCharsets.UTF_8));
        cookie1.setPath("/");
        cookie1.setMaxAge(60 * 60);
        response.addCookie(cookie1);

//        RefreshToken refreshToken1 = new RefreshToken(String.valueOf(loginUser.getId()), refreshToken, accessToken);
//        refreshTokenRepository.save(refreshToken1);
//        RefreshToken foundTokenInfo = refreshTokenRepository.findByAccessToken(accessToken)
//                .orElseThrow();
//        log.info("redis안의 토큰: {}", foundTokenInfo.getRefreshToken());
        return loginResponse;
    }
}

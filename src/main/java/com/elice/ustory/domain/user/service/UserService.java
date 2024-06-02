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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
//    private final RefreshTokenRepository refreshTokenRepository;

    public Users findById(Long userId){
        return userRepository.findById(userId).orElseThrow();
    }

    public Users signUp(SignUpRequest signUpRequest) {

        String email = signUpRequest.getEmail();
        Users.LoginType loginType = Users.LoginType.BASIC;
        String name = signUpRequest.getName();
        String nickname = signUpRequest.getNickname();
        String rawPassword = signUpRequest.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        String profileImg = signUpRequest.getProfileImgUrl();
        String profileDescription = signUpRequest.getProfileDescription();

        Users builtUser = Users.addUserBuilder()
                .email(email)
                .loginType(loginType)
                .name(name)
                .nickname(nickname)
                .password(encodedPassword)
                .profileImgUrl(profileImg)
                .profileDescription(profileDescription)
                .build();

        Users newUser = userRepository.save(builtUser);
        return newUser;
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
        String profileImg = updateRequest.getProfileImgUrl();
        String profileDescription = updateRequest.getProfileDescription();

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
            user.setProfileImgUrl(profileImg);
        }
        if(profileDescription != null) {
            user.setProfileDescription(profileDescription);
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

    public LoginResponse login(String id, String rawPassword, HttpServletResponse response) {
        LoginResponse loginResponse = new LoginResponse();

        //TODO: 예외처리
        Users loginUser = userRepository.findByEmail(id)
                .orElseThrow();
        String encodedPassword = loginUser.getPassword();
        log.info("[getSignInResult] Id : {}", id);

        if(!passwordEncoder.matches(rawPassword, encodedPassword)) {
            loginResponse.builder()
                    .accessToken(null)
                    .refreshToken(null)
                    .build();
            return loginResponse;
        }
        log.info("[getLogInResult] 패스워드 일치");
        log.info("[getLogInResult] LogInResponse 객체 생성");
        String accessToken = jwtTokenProvider.createAccessToken(
                loginUser.getNickname()
        );

        String refreshToken = jwtTokenProvider.createRefreshToken();

        loginResponse = LoginResponse.builder()
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

    public MyPageResponse showMyPage(Long userId) {
        Users currentUser = userRepository.findById(userId)
                .orElseThrow();
        String nickname = currentUser.getNickname();
        String name = currentUser.getName();
        String profileDescription = currentUser.getProfileDescription();
        String profileImgUrl = currentUser.getProfileImgUrl();

        MyPageResponse myPageResponse = MyPageResponse.builder()
                .nickname(nickname)
                .name(name)
                .profileDescription(profileDescription)
                .profileImgUrl(profileImgUrl)
                .build();

        return myPageResponse;
    }

    public ValidateNicknameResponse isValid(ValidateNicknameRequest validateNicknameRequest) {
        String nickname = validateNicknameRequest.getNickname();

        // 중복 여부 확인(false면 합격)
        Boolean isDuplicate = userRepository.findByNickname(nickname).isPresent();

        // 조건 불일치 여부 확인(false면 합격)
        String regex = "[a-zA-Z가-힣]{2,10}";
        Boolean isInappropriate = !nickname.matches(regex);

        // 최종, 닉네임 유효 여부 반환
        ValidateNicknameResponse validateNicknameResponse = ValidateNicknameResponse.builder()
                .isDuplicate(isDuplicate)
                .isInappropriate(isInappropriate)
                .build();

        return validateNicknameResponse;
    }
}

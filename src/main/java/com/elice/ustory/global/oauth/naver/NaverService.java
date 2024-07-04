package com.elice.ustory.global.oauth.naver;

import com.elice.ustory.domain.diary.entity.Color;
import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.diary.entity.DiaryCategory;
import com.elice.ustory.domain.diary.repository.DiaryRepository;
import com.elice.ustory.domain.diaryUser.entity.DiaryUser;
import com.elice.ustory.domain.diaryUser.entity.DiaryUserId;
import com.elice.ustory.domain.diaryUser.repository.DiaryUserRepository;
import com.elice.ustory.domain.user.dto.LoginResponse;
import com.elice.ustory.domain.user.dto.LogoutResponse;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.domain.user.service.UserService;
import com.elice.ustory.global.exception.model.NotFoundException;
import com.elice.ustory.global.jwt.JwtTokenProvider;
import com.elice.ustory.global.jwt.JwtUtil;
import com.elice.ustory.global.redis.naver.NaverTokenService;
import com.elice.ustory.global.redis.refresh.RefreshTokenService;
import com.elice.ustory.global.util.RandomGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NaverService {
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryUserRepository diaryUserRepository;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final NaverTokenService naverTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtUtil jwtUtil;
    private final RandomGenerator randomGenerator;

    public void naverSignUp(String naverNickname){
        String randomPassword = String.valueOf(UUID.randomUUID()).substring(0,8);
        String generatedNickname = naverNickname + "#" + randomGenerator.generateRandomPostfix();

        Users builtUser = Users.addUserBuilder()
                .email(naverNickname+"@ustory.com")
                .loginType(Users.LoginType.NAVER)
                .name(naverNickname)
                .nickname(generatedNickname)
                .password(randomPassword)
                .profileImgUrl("")
                .profileDescription("자기소개")
                .build();

        userRepository.save(builtUser);

        Diary userDiary = new Diary(
                String.format("%s의 다이어리", builtUser.getNickname()),
                "기본 DiaryImgUrl",
                DiaryCategory.INDIVIDUAL,
                String.format("%s의 개인 다이어리", builtUser.getNickname()),
                Color.RED
        );
        diaryRepository.save(userDiary);
        diaryUserRepository.save(new DiaryUser(new DiaryUserId(userDiary,builtUser)));
    }

    public LoginResponse naverLogin(String naverNickname, HttpServletResponse response, String naverToken){
        Users loginUser = userRepository.findByEmail(naverNickname + "@ustory.com")
                .orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다."));

        String accessToken = jwtTokenProvider.createAccessTokenSocial(loginUser.getId(), naverToken, loginUser.getLoginType());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        log.info("[getLogInResult] LogInResponse 객체에 값 주입");
        response.addHeader("Authorization", accessToken);

        refreshTokenService.saveTokenInfo(loginUser.getId(), refreshToken, accessToken, 60 * 60 * 24 * 7);
        naverTokenService.saveNaverTokenInfo(loginUser.getId(), naverToken, accessToken);

        log.info("[logIn] 정상적으로 로그인되었습니다. id : {}, token : {}", loginUser.getId(), loginResponse.getAccessToken());
        return loginResponse;
    }

    public LogoutResponse naverLogout(HttpServletRequest request) {
        String accessToken = jwtUtil.getTokenFromRequest(request);
        naverTokenService.removeNaverTokenInfo(accessToken);
        userService.logout(request);

        return LogoutResponse.builder().success(true).build();
    }
}

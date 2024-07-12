package com.elice.ustory.global.oauth.kakao;

import com.elice.ustory.domain.diary.entity.Color;
import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.diary.entity.DiaryCategory;
import com.elice.ustory.domain.diary.repository.DiaryRepository;
import com.elice.ustory.domain.diaryUser.entity.DiaryUser;
import com.elice.ustory.domain.diaryUser.entity.DiaryUserId;
import com.elice.ustory.domain.diaryUser.repository.DiaryUserRepository;
import com.elice.ustory.domain.user.dto.LoginResponse;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.exception.model.NotFoundException;
import com.elice.ustory.global.jwt.JwtTokenProvider;
import com.elice.ustory.global.jwt.JwtUtil;
import com.elice.ustory.global.redis.kakao.KakaoTokenService;
import com.elice.ustory.global.redis.refresh.RefreshTokenService;
import com.elice.ustory.global.util.NicknameGenerator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoService {
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryUserRepository diaryUserRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final KakaoTokenService kakaoTokenService;
    private final JwtUtil jwtUtil;
    private final KakaoOauth kakaoOauth;
    private final NicknameGenerator nicknameGenerator;
    private final PasswordEncoder passwordEncoder;

    public void kakaoSignUp(String kakaoUserId, String kakaoNickname){
        String randomPassword = String.valueOf(UUID.randomUUID()).substring(0,8);
        String encodedPassword = passwordEncoder.encode(randomPassword);
        String generatedNickname = kakaoNickname + "#" + nicknameGenerator.generateRandomPostfix();

        Users builtUser = Users.addUserBuilder()
                .email(kakaoUserId+"@ustory.com")
                .loginType(Users.LoginType.KAKAO)
                .name(kakaoNickname)
                .nickname(generatedNickname)
                .password(encodedPassword)
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

    public LoginResponse kakaoLogin(String kakaoUserId, HttpServletResponse response, String kakaoToken){
        Users loginUser = userRepository.findByEmail(kakaoUserId+"@ustory.com")
                .orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다."));

        String accessToken = jwtTokenProvider.createAccessTokenSocial(loginUser.getId(), kakaoToken, loginUser.getLoginType());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        log.info("[getLogInResult] LogInResponse 객체에 값 주입");
        response.addHeader("Authorization", accessToken);

        refreshTokenService.saveTokenInfo(loginUser.getId(), refreshToken, accessToken, 60 * 60 * 24 * 7);
        kakaoTokenService.saveKakaoTokenInfo(loginUser.getId(), kakaoToken, accessToken);

        log.info("[logIn] 정상적으로 로그인되었습니다. id : {}, toke n : {}", loginUser.getId(), loginResponse.getAccessToken());
        return loginResponse;
    }

    public void kakaoLogout(String accessToken) {
        String kakaoToken = jwtUtil.getSocialToken(accessToken);
        kakaoOauth.expireKakaoToken(kakaoToken);
        kakaoTokenService.removeKakaoTokenInfo(accessToken);
    }
}

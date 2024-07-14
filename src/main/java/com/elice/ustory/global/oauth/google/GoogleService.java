package com.elice.ustory.global.oauth.google;

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
import com.elice.ustory.global.redis.google.GoogleTokenService;
import com.elice.ustory.global.redis.refresh.RefreshTokenService;
import com.elice.ustory.global.util.NicknameGenerator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleService {
    private final String EMAIL_LITERAL = "email";
    private final String NAME_LITERAL = "name";
    private final String AUTHORIZATION_LITERAL = "Authorization";
    private final String NOT_FOUND_USER_MESSAGE = "해당 유저를 찾을 수 없습니다.";

    private final int REFRESH_TOKEN_TTL = 60 * 60 * 24 * 7;

    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryUserRepository diaryUserRepository;

    private final GoogleTokenService googleTokenService;
    private final RefreshTokenService refreshTokenService;

    private final PasswordEncoder passwordEncoder;
    private final NicknameGenerator nicknameGenerator;
    private final JwtTokenProvider jwtTokenProvider;

    public void googleSignUp(HashMap<String, String> accountProfile) {
        String email = accountProfile.get(EMAIL_LITERAL);
        String name = accountProfile.get(NAME_LITERAL);

        String randomPassword = String.valueOf(UUID.randomUUID()).substring(0, 8);
        String encodedPassword = passwordEncoder.encode(randomPassword);
        String formattedName = nicknameGenerator.formatNickname(name);

        Users builtUser = Users.addUserBuilder()
                .email(email)
                .loginType(Users.LoginType.GOOGLE)
                .name(formattedName)
                .nickname(formattedName)
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
        diaryUserRepository.save(new DiaryUser(new DiaryUserId(userDiary, builtUser)));
    }

    public LoginResponse googleLogin(String googleEmail, HttpServletResponse response, String googleToken) {

        Users loginUser = userRepository.findByEmail(googleEmail)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_MESSAGE));

        String accessToken = jwtTokenProvider.createAccessTokenSocial(loginUser.getId(), googleToken, loginUser.getLoginType());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        log.info("[getLoginResult] LogInResponse 객체에 값 주입");
        response.addHeader(AUTHORIZATION_LITERAL, accessToken);

        refreshTokenService.saveTokenInfo(loginUser.getId(), refreshToken, accessToken, REFRESH_TOKEN_TTL);
        googleTokenService.saveGoogleTokenInfo(loginUser.getId(), googleToken, accessToken);

        log.info("[logIn] 정상적으로 로그인되었습니다. id : {}, token : {}", loginUser.getId(), loginResponse.getAccessToken());
        return loginResponse;
    }

    public void googleLogout(String accessToken) { googleTokenService.removeGoogleTokenInfo(accessToken); }
}

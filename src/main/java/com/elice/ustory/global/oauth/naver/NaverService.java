package com.elice.ustory.global.oauth.naver;

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
import com.elice.ustory.global.redis.naver.NaverTokenService;
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
public class NaverService {
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryUserRepository diaryUserRepository;
    private final RefreshTokenService refreshTokenService;
    private final NaverTokenService naverTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final NicknameGenerator nicknameGenerator;
    private final PasswordEncoder passwordEncoder;

    public void naverSignUp(String naverNickname, String naverEmail){
        String randomPassword = String.valueOf(UUID.randomUUID()).substring(0,8);
        String encodedPassword = passwordEncoder.encode(randomPassword);
        // 닉네임에 포함된 특수문자를 제거한다.
        String cleanedNickname = nicknameGenerator.normalizeNicknameForOAuth(naverNickname);
        // 닉네임을 규정된 길이에 맞게 자른다.
        String trimmedNickname = nicknameGenerator.trimNicknameForOAuth(cleanedNickname);
        // 중복 여부를 조회한다. 중복이면 닉네임을 7자 이내로 자르고, 겹치지 않을 때까지 임의의 postfix 3글자를 만들어 붙인다.
        if (nicknameGenerator.checkDuplicateNickname(trimmedNickname)) {
            trimmedNickname = nicknameGenerator.trimNicknameForPostfix(trimmedNickname);

            do {
                trimmedNickname = trimmedNickname + nicknameGenerator.SEPARATOR + nicknameGenerator.generateRandomPostfix();
            } while (nicknameGenerator.checkDuplicateNickname(trimmedNickname));
        }

        Users builtUser = Users.addUserBuilder()
                .email(naverEmail)
                .loginType(Users.LoginType.NAVER)
                .name(trimmedNickname)
                .nickname(trimmedNickname)
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

    public LoginResponse naverLogin(String naverEmail, HttpServletResponse response, String naverToken){
        Users loginUser = userRepository.findByEmail(naverEmail)
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

    public void naverLogout(String accessToken) {
        naverTokenService.removeNaverTokenInfo(accessToken);
    }
}

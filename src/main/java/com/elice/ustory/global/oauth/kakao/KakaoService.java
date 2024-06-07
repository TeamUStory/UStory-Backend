package com.elice.ustory.global.oauth.kakao;

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
import com.elice.ustory.global.jwt.JwtTokenProvider;
import com.elice.ustory.global.jwt.JwtUtil;
import com.elice.ustory.global.redis.refresh.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;
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
    private final JwtUtil jwtUtil;
    private final KakaoOauth kakaoOauth;
    private final UserService userService;

    public void kakaoSignUp(String kakaoUserId, String kakaoNickname){
        String randomPassword = String.valueOf(UUID.randomUUID()).substring(0,8);
        String generatedNickname = kakaoNickname + "#" + generateRandomPostfix();

        Users builtUser = Users.addUserBuilder()
                .email(kakaoUserId+"@ustory.com")
                .loginType(Users.LoginType.KAKAO)
                .name(kakaoNickname)
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

    public LoginResponse kakaoLogin(String kakaoUserId, HttpServletResponse response, String kakaoToken){
        Users loginUser = userRepository.findByEmail(kakaoUserId+"@ustory.com")
                .orElseThrow();

        String accessToken = jwtTokenProvider.createAccessTokenKakao(loginUser.getId(), kakaoToken);
        String refreshToken = jwtTokenProvider.createRefreshToken();

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        log.info("[getLogInResult] LogInResponse 객체에 값 주입");
        response.addHeader("Authorization", "Bearer " + accessToken);

        refreshTokenService.saveTokenInfo(loginUser.getId(), refreshToken, accessToken, 60 * 60 * 24 * 7);

        log.info("[logIn] 정상적으로 로그인되었습니다. id : {}, token : {}", loginUser.getId(), loginResponse.getAccessToken());
        return loginResponse;
    }

    public LogoutResponse kakaoLogout(HttpServletRequest request) {
        String accessToken = jwtUtil.getTokenFromRequest(request);
        String kakaoToken = jwtTokenProvider.getKakaoToken(accessToken);
        kakaoOauth.expireKakaoToken(kakaoToken);
        userService.logout(request);

        return LogoutResponse.builder().success(true).build();
    }

    public String generateRandomPostfix() {
        int leftLimit = 48; // 숫자 '0'의 ASCII 코드
        int rightLimit = 122; // 알파벳 'z'의 ASCII 코드
        int stringLength = 4;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1) // leftLimit(포함) 부터 rightLimit+1(불포함) 사이의 난수 스트림 생성
                .filter(i -> (i < 57 || i >= 65) && ( i <= 90 || i >= 97)) // ASCII 테이블에서 숫자, 대문자, 소문자만 사용함
                .limit(stringLength) // 생성된 난수를 지정된 길이로 잘라냄
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append) // 생성된 난수를 ASCII 테이블에서 대응되는 문자로 변환
                .toString(); // StringBuilder 객체를 문자열로 변환해 반환
    }
}

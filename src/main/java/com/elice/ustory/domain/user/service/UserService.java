package com.elice.ustory.domain.user.service;

import com.elice.ustory.domain.diary.entity.Color;
import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.diary.entity.DiaryCategory;
import com.elice.ustory.domain.diary.repository.DiaryRepository;
import com.elice.ustory.domain.diaryUser.entity.DiaryUser;
import com.elice.ustory.domain.diaryUser.entity.DiaryUserId;
import com.elice.ustory.domain.diaryUser.repository.DiaryUserRepository;
import com.elice.ustory.domain.user.dto.UserListDTO;
import com.elice.ustory.domain.user.dto.*;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.jwt.JwtTokenProvider;
import com.elice.ustory.global.redis.refresh.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryUserRepository diaryUserRepository;

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public Users findById(Long userId){
        return userRepository.findById(userId).orElseThrow();
    }

    /**
     * 닉네임으로 전체 사용자를 검색합니다.
     *
     * @param nickname 검색할 닉네임
     * @return 검색된 사용자 목록 (옵셔널)
     */
    public Optional<UserListDTO> findUserByNickname(String nickname) {
        return Optional.ofNullable(nickname)
                .filter(name -> !name.isEmpty())
                .flatMap(userRepository::findByNickname)
                .map(u -> UserListDTO.builder()
                        .name(u.getName())
                        .nickname(u.getNickname())
                        .profileImgUrl(u.getProfileImgUrl())
                        .build());
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

        // 개인 다이어리 생성
        Diary userDiary = new Diary(
                String.format("%s의 다이어리", builtUser.getNickname()),
                "기본 DiaryImgUrl",
                DiaryCategory.INDIVIDUAL,
                String.format("%s의 개인 다이어리", builtUser.getNickname()),
                Color.RED
        );
        diaryRepository.save(userDiary);
        diaryUserRepository.save(new DiaryUser(new DiaryUserId(userDiary,builtUser)));

        return newUser;
    }

    public Users updateUser(UpdateRequest updateRequest, Long userId) {
        //TODO: 회원 정보 수정 시 Access Token 재발급 해야함
        //TODO: Optional 예외처리
        Users user = userRepository
                .findById(userId)
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

    public Users deleteUser(Long userId) {

        //TODO: 예외처리
        Users user = userRepository.findById(userId)
                .orElseThrow();

        user.setDeletedAt(LocalDateTime.now());

        Users deletedUser = userRepository.save(user);
        return deletedUser;
    }

    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        String id = loginRequest.getLoginEmail();
        String rawPassword = loginRequest.getPassword();
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
        String accessToken = jwtTokenProvider.createAccessToken(loginUser.getId());

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

        refreshTokenService.saveTokenInfo(loginUser.getId(), refreshToken, accessToken, 60 * 60 * 24 * 7);

        log.info("[logIn] 정상적으로 로그인되었습니다. id : {}, token : {}", id, loginResponse.getAccessToken());
        return loginResponse;
    }

    public LogoutResponse logout(HttpServletRequest request, HttpServletResponse response) {
        // 1. 쿠키 만료 시작
        Cookie cookieForExpire = new Cookie("Authorization", null);
        cookieForExpire.setPath("/");
        cookieForExpire.setMaxAge(0);
        response.addCookie(cookieForExpire); // 생성 즉시 만료되는 쿠키로 덮어씌움
        // 1. 쿠키 만료 끝

        // 2. 리프레시 토큰 삭제 시작
        Cookie currentCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> "Authorization".equals(cookie.getName()))
                .findFirst().orElseThrow();
        String token = URLDecoder.decode(currentCookie.getValue(), StandardCharsets.UTF_8);
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        refreshTokenService.removeTokenInfo(token);
        // 2. 리프레시 토큰 삭제 끝

        LogoutResponse logoutResponse = LogoutResponse.builder().success(true).build();
        return logoutResponse;
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

    public ValidateNicknameResponse isValidNickname(ValidateNicknameRequest validateNicknameRequest) {
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

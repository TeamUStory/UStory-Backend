package com.elice.ustory.domain.user.service;

import com.elice.ustory.domain.diary.entity.Color;
import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.diary.entity.DiaryCategory;
import com.elice.ustory.domain.diary.repository.DiaryRepository;
import com.elice.ustory.domain.diaryUser.entity.DiaryUser;
import com.elice.ustory.domain.diaryUser.entity.DiaryUserId;
import com.elice.ustory.domain.diaryUser.repository.DiaryUserRepository;
import com.elice.ustory.domain.user.dto.FindByNicknameResponse;
import com.elice.ustory.domain.user.dto.*;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.exception.model.NotFoundException;
import com.elice.ustory.global.exception.model.UnauthorizedException;
import com.elice.ustory.global.exception.model.ValidationException;
import com.elice.ustory.global.jwt.JwtTokenProvider;
import com.elice.ustory.global.redis.refresh.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    private static final String NO_AUTHORIZATION_IN_HEADER_MESSAGE = "헤더에 토큰을 입력해주세요.";

    public Users findById(Long userId){
        return userRepository.findById(userId).orElseThrow();
    }

    public FindByNicknameResponse searchUserByNickname(String nickname) {
        Optional<Users> userOptional = userRepository.findByNickname(nickname);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();

            return FindByNicknameResponse.builder()
                    .isExist(true)
                    .name(user.getName())
                    .nickname(user.getNickname())
                    .profileImgUrl(user.getProfileImgUrl())
                    .build();
        } else {
            return FindByNicknameResponse.builder()
                    .isExist(false)
                    .name(null)
                    .nickname(null)
                    .profileImgUrl(null)
                    .build();
        }
    }

    @Transactional
    public Users signUp(SignUpRequest signUpRequest) {

        // 1-0. 입력값 유효성 체크 시작.
        // TODO: 닉네임, 이메일을 인증된 값으로 넘겨준 게 맞는지 한 번 더 확인
        // 1-1. TODO: 닉네임 null 체크(중복여부는 확인된 상태로 넘어옴)

        // 1-2. TODO: 이메일 null 체크(인증 및 중복 여부는 확인된 상태로 넘어옴)

        // TODO: 1-3. 이름 null 체크(현재 별도 조건 없음)

        // TODO: 1-4. 비밀번호 형식 체크

        // 1-5. 비밀번호 일치 체크
        String password = signUpRequest.getPassword();
        String passwordCheck = signUpRequest.getPasswordCheck();
        checkNewPasswordMatch(password, passwordCheck);

        // 1-6. 입력값 유효성 체크 끝

        // 인증된 값으로 유저 생성
        String email = signUpRequest.getEmail();
        Users.LoginType loginType = Users.LoginType.BASIC;
        String name = signUpRequest.getName();
        String nickname = signUpRequest.getNickname();
        String encodedPassword = passwordEncoder.encode(password); // 비밀번호 암호화
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
                .orElseThrow(() -> new NotFoundException("해당 이메일을 가진 유저를 찾을 수 없습니다."));
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
        response.addHeader("Authorization", "Bearer " + accessToken);

        refreshTokenService.saveTokenInfo(loginUser.getId(), refreshToken, accessToken, 60 * 60 * 24 * 7);

        log.info("[logIn] 정상적으로 로그인되었습니다. id : {}, token : {}", id, loginResponse.getAccessToken());
        return loginResponse;
    }

    public LogoutResponse logout(HttpServletRequest request) {
        // 리프레시 토큰 삭제
        String token = request.getHeader("Authorization");

        if (token == null) {
            throw new UnauthorizedException(NO_AUTHORIZATION_IN_HEADER_MESSAGE);
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        refreshTokenService.removeTokenInfo(token);

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

        // 최종, 닉네임 유효 여부 반환
        ValidateNicknameResponse validateNicknameResponse = ValidateNicknameResponse.builder()
                .isDuplicate(isDuplicate)
                .build();

        return validateNicknameResponse;
    }

    public boolean checkExistByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public void checkNewPasswordMatch(String firstEnter, String secondEnter) {
        if (!firstEnter.equals(secondEnter)) {
            throw new ValidationException("비밀번호가 일치하지 않습니다.");
        }
    }
}
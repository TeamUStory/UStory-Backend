package com.elice.ustory.domain.user.service;

import com.elice.ustory.domain.user.constant.UserMessageConstants;
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
import com.elice.ustory.global.exception.model.*;
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

    public Users findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(UserMessageConstants.NOT_FOUND_USER_ID_MESSAGE, userId)));
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
        // 1-0. 입력값 유효성 체크 시작

        // 1-1. 닉네임 유효 재확인
        String nickname = signUpRequest.getNickname();
        ValidateNicknameRequest validateNicknameRequest = new ValidateNicknameRequest();
        validateNicknameRequest.setNickname(nickname);
        if (isValidNickname(validateNicknameRequest).getIsValid() == false) {
            throw new ValidationException(String.format(UserMessageConstants.NOT_VALID_NICKNAME_MESSAGE, nickname));
        };

        // 1-2. 이메일 중복 재확인
        String email = signUpRequest.getEmail();

        int emailCountWithSoftDeleted = userRepository.countByEmailWithSoftDeleted(email);
        if (emailCountWithSoftDeleted > 0) {
            throw new ConflictException(String.format(UserMessageConstants.DUPLICATE_EMAIL_MESSAGE, email));
        }

        // 1-3. 이름 확인 (현재 별도 조건 없음)
        String name = signUpRequest.getName();

        // 1-4. 비밀번호 일치 체크
        String password = signUpRequest.getPassword();
        String passwordCheck = signUpRequest.getPasswordCheck();
        checkNewPasswordMatch(password, passwordCheck);

        // 1-5. 입력값 유효성 체크 끝

        // 인증된 값으로 유저 생성
        Users.LoginType loginType = Users.LoginType.BASIC;
        String encodedPassword = passwordEncoder.encode(password); // 비밀번호 암호화
        String profileImgUrl = signUpRequest.getProfileImgUrl();
        String profileDescription = signUpRequest.getProfileDescription();
        String diaryImgUrl = signUpRequest.getDiaryImgUrl();

        Users builtUser = Users.addUserBuilder()
                .email(email)
                .loginType(loginType)
                .name(name)
                .nickname(nickname)
                .password(encodedPassword)
                .profileImgUrl(profileImgUrl)
                .profileDescription(profileDescription)
                .build();

        Users newUser = userRepository.save(builtUser);

        // 개인 다이어리 생성
        try {
            Diary userDiary = new Diary(
                    String.format("%s의 다이어리", builtUser.getNickname()),
                    diaryImgUrl,
                    DiaryCategory.INDIVIDUAL,
                    String.format("%s의 개인 다이어리", builtUser.getNickname()),
                    Color.RED
            );
            diaryRepository.save(userDiary);
            diaryUserRepository.save(new DiaryUser(new DiaryUserId(userDiary, builtUser)));
        } catch (Exception e) {
            throw new InternalServerException(String.format(UserMessageConstants.NOT_CREATED_DIARY_MESSAGE, email));
        }

        return newUser;
    }

    @Transactional
    public Users updateUser(UpdateRequest updateRequest, Long userId) {
        //TODO: 회원 정보 수정 시 Access Token 재발급 해야함
        Users user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(UserMessageConstants.NOT_FOUND_USER_ID_MESSAGE, userId)));

        String name = updateRequest.getName();
        String nickname = updateRequest.getNickname();
        String profileImgUrl = updateRequest.getProfileImgUrl();
        String profileDescription = updateRequest.getProfileDescription();

        if (name != null) {
            user.setName(name);
        }
        if (nickname != null) {
            user.setNickname(nickname);
        }
        if (profileImgUrl != null) {
            user.setProfileImgUrl(profileImgUrl);
        }
        if (profileDescription != null) {
            user.setProfileDescription(profileDescription);
        }

        Users updatedUser = userRepository.save(user);
        return updatedUser;
    }

    public void updateLostPassword(Long userId, ChangePwdRequest changePwdRequest) {
        Users currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(UserMessageConstants.NOT_FOUND_USER_ID_MESSAGE, userId)));

        // 입력된 비밀번호 두 개의 일치 여부 확인 후, 다르면 에러 반환
        String password = changePwdRequest.getPassword();
        String passwordCheck = changePwdRequest.getPasswordCheck();
        checkNewPasswordMatch(password, passwordCheck);

        // 수정(암호화해서 저장)
        String encodedPassword = passwordEncoder.encode(password);
        currentUser.setPassword(encodedPassword);
        userRepository.save(currentUser);

        //토큰 만료
    }

    public Users deleteUser(Long userId) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(UserMessageConstants.NOT_FOUND_USER_ID_MESSAGE, userId)));

        user.setDeletedAt(LocalDateTime.now());

        Users deletedUser = userRepository.save(user);
        return deletedUser;
    }

    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        String id = loginRequest.getLoginEmail();
        String rawPassword = loginRequest.getPassword();
        LoginResponse loginResponse = new LoginResponse();

        Users loginUser = userRepository.findByEmail(id)
                .orElseThrow(() -> new NotFoundException(String.format(UserMessageConstants.NOT_FOUND_USER_EMAIL_MESSAGE, id)));
        String encodedPassword = loginUser.getPassword();
        log.info("[getSignInResult] Id : {}", id);

        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
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
        response.addHeader("Authorization", accessToken);

        refreshTokenService.saveTokenInfo(loginUser.getId(), refreshToken, accessToken, 60 * 60 * 24 * 7);

        log.info("[logIn] 정상적으로 로그인되었습니다. id : {}, token : {}", id, loginResponse.getAccessToken());
        return loginResponse;
    }

    public LogoutResponse logout(HttpServletRequest request) {
        // 리프레시 토큰 삭제
        String token = request.getHeader("Authorization");

        if (token == null) {
            throw new UnauthorizedException(UserMessageConstants.UNAUTHORIZED_MESSAGE);
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
                .orElseThrow(() -> new NotFoundException(String.format(UserMessageConstants.NOT_FOUND_USER_ID_MESSAGE, userId)));
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

        int nicknameCountWithSoftDeleted = userRepository.countByNicknameWithSoftDeleted(nickname);
        if (nicknameCountWithSoftDeleted > 0) {
            return ValidateNicknameResponse.builder()
                    .isValid(false)
                    .isDuplicate(true)
                    .build();
        } else {
            return ValidateNicknameResponse.builder()
                    .isValid(true)
                    .isDuplicate(false)
                    .build();
        }
    }

    public boolean checkExistByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void checkNewPasswordMatch(String firstEnter, String secondEnter) {
        if (!firstEnter.equals(secondEnter)) {
            throw new ValidationException(UserMessageConstants.NOT_VALID_PASSWORD_MESSAGE);
        }
    }

}
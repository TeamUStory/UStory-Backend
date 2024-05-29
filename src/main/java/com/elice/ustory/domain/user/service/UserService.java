package com.elice.ustory.domain.user.service;

import com.elice.ustory.domain.user.dto.*;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public Users signUp(SignUpRequest signUpRequest) {

        String email = signUpRequest.getEmail();
        String name = signUpRequest.getName();
        String nickname = signUpRequest.getNickname();
        String password = signUpRequest.getPassword();
        String profileImg = signUpRequest.getProfileImg();

        Users builtUser = Users.addUserBuilder()
                .email(email)
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

    public LoginResponse login(String id, String password) {
        //TODO: 예외처리
        Users loginUser = userRepository.findByEmail(id)
                .orElseThrow();
        log.info("[getSignInResult] Id : {}", id);

        if (!loginUser.getPassword().equals(password)) {
            //TODO: 비밀번호 오류 예외처리
            return null;
        }

        log.info("[getLogInResult] 패스워드 일치");
        log.info("[getLogInResult] LogInResultDto 객체 생성");

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(jwtTokenProvider.createAccessToken(
                        loginUser.getNickname(),
                        loginUser.getLoginType()
                        ))
                .build();

        log.info("[getLogInResult] LogInResultDto 객체에 값 주입");

        return loginResponse;
    }
}

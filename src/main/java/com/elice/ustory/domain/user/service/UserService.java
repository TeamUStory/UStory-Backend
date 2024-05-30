package com.elice.ustory.domain.user.service;

import com.elice.ustory.domain.user.dto.*;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
//TODO: uri를 설정파일 밖에 상수값으로 두기
//TODO: 리프레시토큰 만료시. (에러코드 401?, 로컬에 저장된 액세스 토큰) 쿠키의 토큰을 서버가 지울 수 있나? - 쿠키가 삭제되면 토큰도 무효화

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public Users signUp(SignUpRequest signUpRequest) {

        String email = signUpRequest.getEmail();
        Users.LoginType loginType = Users.LoginType.BASIC;
        String name = signUpRequest.getName();
        String nickname = signUpRequest.getNickname();
        String password = signUpRequest.getPassword();
        String profileImg = signUpRequest.getProfileImg();

        Users builtUser = Users.addUserBuilder()
                .email(email)
                .loginType(loginType)
                .name(name)
                .nickname(nickname)
                .password(password)
                .profileImgUrl(profileImg)
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
            user.setProfileImgUrl(profileImg);
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

        if (!loginUser.getPassword().equals(password)) {
            //TODO: 비밀번호 오류 예외처리
            return null;
        }

        LoginResponse loginResponse = LoginResponse.builder()
                .token(jwtTokenProvider.createAccessToken(
                        loginUser.getNickname(),
                        loginUser.getLoginType()
                        ))
                .build();

        return loginResponse;
    }
}

package com.elice.ustory.domain.user.service;

import com.elice.ustory.domain.user.dto.SignUpRequest;
import com.elice.ustory.domain.user.dto.SignUpResponse;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public ResponseEntity<SignUpResponse> signUp(SignUpRequest signUpRequest) {

        String email = signUpRequest.getEmail();
        String name = signUpRequest.getName();
        String nickname = signUpRequest.getNickname();
        String password = signUpRequest.getPassword();
        String profileImg = signUpRequest.getProfileImg();

        Users newUser = Users.addUserBuilder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .password(password)
                .profileImg(profileImg)
                .build();

        userRepository.save(newUser);
        return SignUpResponse.success();
    }
}

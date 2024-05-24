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

        Users newUser = Users.builder()
                .email(signUpRequest.getEmail())
                .name(signUpRequest.getName())
                .nickname(signUpRequest.getNickname())
                .password(signUpRequest.getPassword())
                .profileImg(signUpRequest.getProfileImg())
                .build();

        userRepository.save(newUser);
        return SignUpResponse.success();
    }
}

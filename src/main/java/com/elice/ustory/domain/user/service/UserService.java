package com.elice.ustory.domain.user.service;

import com.elice.ustory.domain.user.dto.*;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

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

    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        return (UserDetails) userRepository.findByNickname(nickname).orElseThrow(() -> new UsernameNotFoundException(nickname));
    }
}

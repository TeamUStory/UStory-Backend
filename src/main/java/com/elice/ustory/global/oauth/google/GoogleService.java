package com.elice.ustory.global.oauth.google;

import com.elice.ustory.domain.diary.entity.Color;
import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.diary.entity.DiaryCategory;
import com.elice.ustory.domain.diary.repository.DiaryRepository;
import com.elice.ustory.domain.diaryUser.entity.DiaryUser;
import com.elice.ustory.domain.diaryUser.entity.DiaryUserId;
import com.elice.ustory.domain.diaryUser.repository.DiaryUserRepository;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.util.NicknameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleService {
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryUserRepository diaryUserRepository;

    private final PasswordEncoder passwordEncoder;
    private final NicknameGenerator nicknameGenerator;

    public void googleSignUp(HashMap<String, String> accountProfile) {
        String email = accountProfile.get("email");
        String name = accountProfile.get("name");

        String randomPassword = String.valueOf(UUID.randomUUID()).substring(0, 8);
        String encodedPassword = passwordEncoder.encode(randomPassword);
        String formattedName = nicknameGenerator.formatNickname(name);

        Users builtUser = Users.addUserBuilder()
                .email(email)
                .loginType(Users.LoginType.GOOGLE)
                .name(formattedName)
                .nickname(formattedName)
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
        diaryUserRepository.save(new DiaryUser(new DiaryUserId(userDiary, builtUser)));
    }
}

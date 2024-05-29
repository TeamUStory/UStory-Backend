package com.elice.ustory.domain.diary.service;

import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.diary.repository.DiaryRepository;
import com.elice.ustory.domain.diaryUser.entity.DiaryUser;
import com.elice.ustory.domain.diaryUser.entity.DiaryUserId;
import com.elice.ustory.domain.diaryUser.repository.DiaryUserRepository;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final DiaryUserRepository diaryUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public Diary createDiary(Diary diary, List<String> users) {
        Diary savedDiary = diaryRepository.save(diary);
        for (String nickname : users) {
            Users user = userRepository.findByNickname(nickname).orElse(null);

            DiaryUserId diaryUserId = new DiaryUserId(savedDiary, user);
            diaryUserRepository.save(new DiaryUser(diaryUserId));
        }

        return savedDiary;
    }

    public Diary getDiaryById(Long id) {
        return id!=null ? diaryRepository.findById(id).orElse(null) : null;
    }

    public Diary updateDiary(Long id, Diary diary, List<String> users) {
        Diary updatedDiary = diaryRepository.findById(id).orElse(null);
        if (updatedDiary == null) return null;
        updatedDiary.updateDiary(diary);

        // 다이어리에 유저가 추가된 경우
        if(users.size()!=diaryUserRepository.countUserByDiary(id)){
            List<String> userList = diaryUserRepository.findUserByDiary(id);
            for (String nickname : users) {
                if(userList.contains(nickname)) continue;

                Users user = userRepository.findByNickname(nickname).orElse(null);
                DiaryUserId diaryUserId = new DiaryUserId(diary, user);
                diaryUserRepository.save(new DiaryUser(diaryUserId));
            }
        }
        return updatedDiary;
    }

    public void deleteDiary(Long id) {
        Diary diary = diaryRepository.findById(id).orElse(null);
        if (diary == null) return;
        diaryRepository.delete(diary);
    }

    // TODO: userId와 diaryName을 통해 DiaryList 반환

}
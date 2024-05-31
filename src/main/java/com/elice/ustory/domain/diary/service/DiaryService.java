package com.elice.ustory.domain.diary.service;

import com.elice.ustory.domain.diary.dto.DiaryList;
import com.elice.ustory.domain.diary.dto.DiaryListResponse;
import com.elice.ustory.domain.diary.dto.DiaryResponse;
import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.diary.entity.DiaryCategory;
import com.elice.ustory.domain.diary.repository.DiaryRepository;
import com.elice.ustory.domain.diaryUser.entity.DiaryUser;
import com.elice.ustory.domain.diaryUser.entity.DiaryUserId;
import com.elice.ustory.domain.diaryUser.repository.DiaryUserRepository;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final DiaryUserRepository diaryUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public DiaryResponse createDiary(Diary diary, List<String> users) {
        Diary savedDiary = diaryRepository.save(diary);
        for (String nickname : users) {
            Users user = userRepository.findByNickname(nickname).orElse(null);
            // TODO : user가 null 인 경우 Exception

            DiaryUserId diaryUserId = new DiaryUserId(savedDiary, user);
            diaryUserRepository.save(new DiaryUser(diaryUserId));
        }

        return DiaryResponse.toDiaryResponse(savedDiary);
    }

    public DiaryResponse getDiaryById(Long id) {
        Diary diary = diaryRepository.findById(id).orElse(null);
        return diary != null ? DiaryResponse.toDiaryResponse(diary) : null;
    }

    @Transactional
    public DiaryResponse updateDiary(Long id, Diary diary, List<String> users) {
        Diary updatedDiary = diaryRepository.findById(id).orElse(null);
        if (updatedDiary == null) return null;
        updatedDiary.updateDiary(diary);

        // TODO : 이미 다이어리에 10명이 존재할 경우 & 기존 유저가 아닌 새로운 10명으로 보내졌을 경우(?)
        // TODO : 기존 멤버가 제외된 리스트가 보내진 경우... & 친구가 아닌 유저가 온 경우...

        // 다이어리에 유저가 추가된 경우
        if (users.size() != diaryUserRepository.countUserByDiary(id)) {
            List<String> userList = diaryUserRepository.findUserByDiary(id);
            for (String nickname : users) {
                if (userList.contains(nickname)) continue;

                Users user = userRepository.findByNickname(nickname).orElse(null);
                // TODO : null(존재하지 않는 유저) -> Exception
                DiaryUserId diaryUserId = new DiaryUserId(updatedDiary, user);
                diaryUserRepository.save(new DiaryUser(diaryUserId));
            }
        }
        return DiaryResponse.toDiaryResponse(updatedDiary);
    }

    public Page<DiaryListResponse> getUserDiaries(Long userId, Pageable pageable, DiaryCategory diaryCategory) {
        if (userId == null) {
            // TODO : EXCEPTION 처리
            return null;
        }
        Page<DiaryList> diaryList = diaryUserRepository.searchDiary(userId, pageable, diaryCategory);
        List<DiaryListResponse> result = diaryList.getContent().stream()
                .map(DiaryList::toDiaryListResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(result, diaryList.getPageable(), diaryList.getTotalElements());
    }

    public List<DiaryListResponse> getUserDiaryList(Long userId) {
        List<DiaryList> result = diaryUserRepository.searchDiaryList(userId);
        return result.stream()
                .map(DiaryList::toDiaryListResponse)
                .collect(Collectors.toList());
    }

    public Long getDiaryCount(Long userId) {
        return diaryUserRepository.countDiaryByUser(userId);
    }

    public void deleteDiary(Long id) {
        Diary diary = diaryRepository.findById(id).orElse(null);
        if (diary == null) return;
        diaryRepository.delete(diary);
    }

    public void exitDiary(Long diaryId, Long userId) {
        Diary diary = diaryRepository.findById(diaryId).orElse(null);
        Users users = userRepository.findById(userId).orElse(null);
        if (diary != null && users != null) {
            DiaryUserId diaryUserId = new DiaryUserId(diary, users);
            DiaryUser diaryUser = diaryUserRepository.findById(diaryUserId).orElse(null);
            diaryUserRepository.delete(diaryUser);
        }
    }

}
package com.elice.ustory.domain.diary.service;

import com.elice.ustory.domain.diary.dto.DiaryList;
import com.elice.ustory.domain.diary.dto.DiaryListResponse;
import com.elice.ustory.domain.diary.dto.DiaryResponse;
import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.diary.entity.DiaryCategory;
import com.elice.ustory.domain.diary.entity.QDiary;
import com.elice.ustory.domain.diary.repository.DiaryRepository;
import com.elice.ustory.domain.diaryUser.entity.DiaryUser;
import com.elice.ustory.domain.diaryUser.entity.DiaryUserId;
import com.elice.ustory.domain.diaryUser.repository.DiaryUserRepository;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.elice.ustory.domain.user.entity.QUsers.users;

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

    public DiaryResponse getDiaryDetailById(Long id) {
        Diary diary = diaryRepository.findById(id).orElse(null);
        return diary != null ? DiaryResponse.toDiaryResponse(diary) : null;
    }

    public Diary getDiaryById(Long id) {
        return diaryRepository.findById(id).orElse(null);
    }

    @Transactional
    public DiaryResponse updateDiary(Long userId, Long diaryId, Diary diary, List<String> userList) {
        Diary updatedDiary = diaryRepository.findById(diaryId).orElse(null);
        if (updatedDiary == null) return null;
        updatedDiary.updateDiary(diary);

        // 다이어리에 유저가 추가된 경우
        if (userList.size() >= diaryUserRepository.countUserByDiary(diaryId)) {
            List<Tuple> usersByDiary = diaryUserRepository.findUsersByDiary(userId, diaryId, userList);
            if(usersByDiary.size()>9 || usersByDiary.size()<userList.size()){
                // TODO : throws EXCEPTION -> request를 보낸 유저까지 11명이 되는 케이스

                // TODO : throws EXCEPTION -> 존재하지 않는 유저 닉네임이 보내진 경우
            }
            for (Tuple tuple : usersByDiary) {
                Users user = tuple.get(users);
                if(tuple.get(QDiary.diary.id)!=null){
                    if(!userList.contains(user.getNickname())){
                        // TODO : throws Exception -> 기존 유저가 사라진 케이스
                    }
                }else{
                    DiaryUserId diaryUserId = new DiaryUserId(updatedDiary, user);
                    diaryUserRepository.save(new DiaryUser(diaryUserId));
                }
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
        // TODO : diary가 비워진 경우 소프트 딜리트(?)
    }

}
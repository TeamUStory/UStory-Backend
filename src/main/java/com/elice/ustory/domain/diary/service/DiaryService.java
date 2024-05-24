package com.elice.ustory.domain.diary.service;

import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.diary.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;

    @Transactional
    public Diary createDiary(Diary diary) {
        return diaryRepository.save(diary);
    }

    @Transactional(readOnly = true)
    public Diary getDiaryById(Long id) {
        Diary findDiary = diaryRepository.findById(id).orElse(null);
        return findDiary;
    }

    @Transactional
    public Diary updateImg(Long id, String diaryImg) {
        Diary diary = diaryRepository.findById(id).orElse(null);
        if (diary == null || diaryImg.trim().isEmpty()) return null;
        diary.setDiaryImg(diaryImg);
        return diary;
    }

    @Transactional
    public Diary updateName(Long id, String name) {
        Diary diary = diaryRepository.findById(id).orElse(null);
        if (diary == null || name.trim().isEmpty()) return null;
        diary.setName(name);
        return diary;
    }

    @Transactional
    public Diary updateDesc(Long id, String description) {
        Diary diary = diaryRepository.findById(id).orElse(null);
        if (diary == null) return null;
        diary.setDescription(description);
        return diary;
    }

    @Transactional
    public void deleteDiary(Long id) {
        Diary diary = diaryRepository.findById(id).orElse(null);
        if (diary == null) return;
        diaryRepository.delete(diary);
    }

    // TODO: userId와 diaryName을 통해 DiaryList 반환

}
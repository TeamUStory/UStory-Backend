package com.elice.ustory.domain.diary.service;

import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.diary.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;

    public Diary createDiary(Diary diary) {
        return diaryRepository.save(diary);
    }

    public Diary getDiaryById(Long id) {
        Diary findDiary = diaryRepository.findById(id).orElse(null);
        return findDiary;
    }

    public Diary updateDiary(Long id, Diary diary){
        Diary updateDiary = diaryRepository.findById(id).orElse(null);
        if(updateDiary==null) return null;

        updateDiary.updateDiary(diary);
        return updateDiary;
    }

    public void deleteDiary(Long id) {
        Diary diary = diaryRepository.findById(id).orElse(null);
        if (diary == null) return;
        diaryRepository.delete(diary);
    }

    // TODO: userId와 diaryName을 통해 DiaryList 반환

}
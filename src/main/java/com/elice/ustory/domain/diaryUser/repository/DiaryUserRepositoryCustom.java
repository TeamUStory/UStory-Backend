package com.elice.ustory.domain.diaryUser.repository;

import com.elice.ustory.domain.diary.dto.DiaryListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DiaryUserRepositoryCustom {
    List<DiaryListResponse> searchListDiary(Long userId);
    Long countUserByDiary(Long diaryId);
    List<String> findUserByDiary(Long diaryId);
    Page<DiaryListResponse> searchDiary(Long userId, Pageable pageable);

}

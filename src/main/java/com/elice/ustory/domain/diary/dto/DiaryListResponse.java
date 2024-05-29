package com.elice.ustory.domain.diary.dto;

import com.elice.ustory.domain.diary.entity.DiaryCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryListResponse {
    private Long id;

    private String name;

    private String diaryImg;

    private DiaryCategory diaryCategory;

    public DiaryListResponse(Long id, String name, String diaryImg, DiaryCategory diaryCategory) {
        this.id = id;
        this.name = name;
        this.diaryImg = diaryImg;
        this.diaryCategory = diaryCategory;
    }
}

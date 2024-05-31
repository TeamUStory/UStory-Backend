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

    private String imgUrl;

    private String diaryCategory;

    public DiaryListResponse(Long id, String name, String imgUrl, String diaryCategory) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
        this.diaryCategory = diaryCategory;
    }
}

package com.elice.ustory.domain.diary.dto;

import com.elice.ustory.domain.diary.entity.DiaryCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryList {
    private Long id;

    private String name;

    private String imgUrl;

    private DiaryCategory diaryCategory;
    public DiaryListResponse toDiaryListResponse(){
        return new DiaryListResponse(this.id,this.name,this.imgUrl, this.diaryCategory.getName());
    }

    public DiaryList(Long id, String name, String imgUrl, DiaryCategory diaryCategory) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
        this.diaryCategory = diaryCategory;
    }
}

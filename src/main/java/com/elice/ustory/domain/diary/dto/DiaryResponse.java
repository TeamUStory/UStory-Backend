package com.elice.ustory.domain.diary.dto;

import com.elice.ustory.domain.diary.entity.Diary;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryResponse {
    private Long id;

    private String name;

    private String imgUrl;

    private String diaryCategory;

    private String description;

    private String color;

    public DiaryResponse(Long id, String name, String imgUrl, String diaryCategory, String description, String color) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
        this.diaryCategory = diaryCategory;
        this.description = description;
        this.color = color;
    }

    public static DiaryResponse toDiaryResponse(Diary diary){
        return new DiaryResponse(
                diary.getId(),
                diary.getName(),
                diary.getImgUrl(),
                diary.getDiaryCategory().getName(),
                diary.getDescription(),
                diary.getColor().getHexCode());
    }

}

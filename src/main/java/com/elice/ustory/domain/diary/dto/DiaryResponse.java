package com.elice.ustory.domain.diary.dto;

import com.elice.ustory.domain.diary.entity.Diary;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryResponse {
    @Schema(description = "다이어리 아이디", example = "3")
    private Long id;

    @Schema(description = "다이어리 이름", example = "꽁냥껑냥")
    private String name;

    @Schema(description = "다이어리 이미지 URL", example = "http://image.com")
    private String imgUrl;

    @Schema(description = "다이어리 분류", example = "친구")
    private String diaryCategory;

    @Schema(description = "다이어리 소개", example = "껑냥이들의 다이어리")
    private String description;

    @Schema(description = "다이어리 마커 색", example = "#FFFFFF")
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

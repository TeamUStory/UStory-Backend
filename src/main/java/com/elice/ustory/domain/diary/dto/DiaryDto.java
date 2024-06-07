package com.elice.ustory.domain.diary.dto;

import com.elice.ustory.domain.diary.entity.Color;
import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.diary.entity.DiaryCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryDto {
    @Schema(description = "다이어리 이름", example = "꽁냥껑냥")
    @NotNull(message = "다이어리 이름은 필수 입니다.")
    @Size(min = 2, max = 40, message = "다이어리 이름은 2~40자 사이만 가능합니다.")
    private String name;

    @Schema(description = "다이어리 이미지 URL", example = "http://image.com")
    @NotNull(message = "다이어리 이미지는 필수 입니다.")
    private String imgUrl;

    @Schema(description = "다이어리 분류", example = "FRIEND")
    @NotNull(message = "다이어리 카테고리는 필수 입니다.")
    private DiaryCategory diaryCategory;

    @Schema(description = "다이어리 소개", example = "껑냥이들의 다이어리")
    @NotNull(message = "다이어리 소개는 필수 입니다.")
    @Size(min = 1, max = 255,message = "다이어리 소개는 1자 이상이어야 합니다.")
    private String description;

    @Schema(description = "다이어리 마커 색", example = "ORANGE")
    @NotNull(message = "다이어리 색은 필수 입니다.")
    private Color color;

    @Schema(description = "다이어리에 초대할 친구 리스트", example = "[\"Marceline\",\"Grace\",\"Bonnibel\",\"Jake\",\"Finn\"]")
    @NotEmpty(message = "친구 목록은 필수 입니다.")
    private List<String> users;

    public DiaryDto(String name, String imgUrl, DiaryCategory diaryCategory, String description, Color color, List<String> users) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.diaryCategory = diaryCategory;
        this.description = description;
        this.color = color;
        this.users = users;
    }

    public Diary toDiary() {
        Diary diary = new Diary(name, imgUrl, diaryCategory, description, color);
        return diary;
    }
}

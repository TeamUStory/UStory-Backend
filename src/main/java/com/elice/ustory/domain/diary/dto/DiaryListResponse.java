package com.elice.ustory.domain.diary.dto;

import com.elice.ustory.domain.diary.entity.DiaryCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryListResponse {
    @Schema(description = "다이어리 아이디", example = "3")
    private Long id;

    @Schema(description = "다이어리 이름", example = "꽁냥껑냥")
    private String name;

    @Schema(description = "다이어리 이미지 URL", example = "http://image.com")
    private String imgUrl;

    @Schema(description = "다이어리 분류", example = "친구")
    private String diaryCategory;

    public DiaryListResponse(Long id, String name, String imgUrl, String diaryCategory) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
        this.diaryCategory = diaryCategory;
    }
}

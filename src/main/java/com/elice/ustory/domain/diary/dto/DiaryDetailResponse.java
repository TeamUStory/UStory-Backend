package com.elice.ustory.domain.diary.dto;

import com.elice.ustory.domain.diary.entity.Diary;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class DiaryDetailResponse {
    @Schema(description = "다이어리 아이디", example = "3")
    private Long id;

    @Schema(description = "다이어리 이름", example = "꽁냥껑냥")
    private String name;

    @Schema(description = "다이어리 이미지 URL", example = "http://image.com")
    private String imgUrl;

    @Schema(description = "다이어리 분류", example = "친구")
    private String diaryCategory;

    @Schema(description = "다이어리 멤버", example = "[\"친구1\", \"친구2\"]")
    private List<DiaryFriend> diaryFriends;

    @Schema(description = "다이어리 마커 색", example = "#FFFFFF")
    private String color;

    public DiaryDetailResponse(Long id, String name, String imgUrl, String diaryCategory,String color, List<DiaryFriend> diaryFriends) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
        this.diaryCategory = diaryCategory;
        this.color=color;
        this.diaryFriends = diaryFriends;
    }

    public static DiaryDetailResponse toDiaryDetailResponse(Diary diary, List<DiaryFriend> diaryFriends){
        return new DiaryDetailResponse(
                diary.getId(),
                diary.getName(),
                diary.getImgUrl(),
                diary.getDiaryCategory().getName(),
                diary.getColor().getHexCode(),
                diaryFriends
        );
    }
}

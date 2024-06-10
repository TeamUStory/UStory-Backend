package com.elice.ustory.domain.paper.dto;

import com.elice.ustory.domain.image.Image;
import com.elice.ustory.domain.paper.entity.Paper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PaperResponse {

    @Schema(description = "타이틀", example = "우규 갔다왔어요.")
    private String title;

    @Schema(description = "썸네일 URL", example = "https://~~~~~")
    private String thumbnailImageUrl;

    @Schema(description = "이미지 URL 리스트", example = "[\"https://~\", \"https://~\"]")
    private List<String> imageUrls;

    @Schema(description = "방문 날짜", example = "2024.05.23 (목)")
    @JsonSerialize(using = LocalDateConverter.class)
    private LocalDate visitedAt;

    @Schema(description = "도로 주소", example = "서울특별시 마포구 독막로3길 21")
    private String city;

    @Schema(description = "상호명", example = "우규")
    private String store;

    @Schema(description = "다이어리명", example = "껌냥이들")
    private String diaryName;

    @Schema(description =
            "저장 여부 <br>" +
            "0을 반환하는 경우 미저장 상태이다. <br>" +
            "1을 반환하는 경우 저장 상태이다.",
            example = "1")
    private Integer bookmarked;

    @Schema(description =
            "잠김 여부 <br>" +
            "0을 반환하는 경우 잠금 상태이며, 필수적인 필드의 값만 반환한다. <br>" +
            "1을 반환하는 경우 해금 상태이며, 모든 필드 값을 반환한다.",
            example = "1")
    private Integer unlocked;

    private Integer isUpdatable;

    public PaperResponse(Paper paper, Boolean bookmarked, Long userId) {

        this.title = paper.getTitle();
        this.thumbnailImageUrl = paper.getThumbnailImageUrl();
        this.visitedAt = paper.getVisitedAt();
        this.city = paper.getAddress().getCity();
        this.store = paper.getAddress().getStore();
        this.unlocked = paper.getUnLocked();
        this.diaryName = paper.getDiary().getName();

        if (bookmarked) {
            this.bookmarked = 1;
        } else {
            this.bookmarked = 0;
        }

        if (paper.isUnlocked()) {
            this.imageUrls = paper.getImages().stream()
                    .map(Image::getImageUrl)
                    .collect(Collectors.toList());
        }

        if (paper.getWriter().getId() == userId) {
            this.isUpdatable = 1;
        } else {
            this.isUpdatable = 0;
        }
    }

}

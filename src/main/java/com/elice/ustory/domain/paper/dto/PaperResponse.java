package com.elice.ustory.domain.paper.dto;

import com.elice.ustory.domain.image.Image;
import com.elice.ustory.domain.paper.entity.Paper;
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

    @Schema(description = "방문 날짜", example = "2024-05-24")
    private LocalDate visitedAt;

    @Schema(description = "도로 주소", example = "서울특별시 마포구 독막로3길 21")
    private String city;

    @Schema(description = "상호명", example = "우규")
    private String store;

    private Integer unlocked;

    public PaperResponse(Paper paper) {
        if (paper.isUnlocked()) {
            unlockedStatus(paper);
        } else {
            lockStatus(paper);
        }
    }

    private void unlockedStatus(Paper paper) {
        this.title = paper.getTitle();
        this.thumbnailImageUrl = paper.getThumbnailImageUrl();
        this.imageUrls = paper.getImages().stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());
        this.visitedAt = paper.getVisitedAt();
        this.city = paper.getAddress().getCity();
        this.store = paper.getAddress().getStore();
        this.unlocked = paper.getUnLocked();
    }

    private void lockStatus(Paper paper) {
        this.title = paper.getTitle();
        this.thumbnailImageUrl = paper.getThumbnailImageUrl();
        this.visitedAt = paper.getVisitedAt();
        this.city = paper.getAddress().getCity();
        this.store = paper.getAddress().getStore();
        this.unlocked = paper.getUnLocked();
    }
}

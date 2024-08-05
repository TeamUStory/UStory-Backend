package com.elice.ustory.domain.great.dto;

import com.elice.ustory.domain.paper.entity.Paper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class GreatListResponse {

    @Schema(description = "타이틀", example = "우규 갔다 왔어요.")
    private String title;

    @Schema(description = "썸네일 이미지", example = "https://~~~~~~")
    private String thumbnailImageUrl;

    @Schema(description = "방문일", example = "2024-05-30")
    private LocalDate visitedAt;

    @Schema(description = "상호명", example = "우규")
    private String store;

    @Schema(description = "paper Id", example = "12345678")
    private Long paperId;

    public GreatListResponse(Paper paper) {
        this.title = paper.getTitle();
        this.thumbnailImageUrl = paper.getThumbnailImageUrl();
        this.visitedAt = paper.getVisitedAt();
        this.store = paper.getAddress().getStore();
        this.paperId = paper.getId();
    }
}

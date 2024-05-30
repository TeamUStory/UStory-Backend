package com.elice.ustory.domain.paper.dto;

import com.elice.ustory.domain.paper.entity.Paper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

// TODO : 메인으로 가는 Response와 나머지 Response가 형식이 다르다. null 값으로 알아서 체킹? 아니면 하나 더 만듦?
@Getter
public class PaperListResponse {

    @Schema(description = "타이틀", example = "우규 갔다 왔어요.")
    private String title;

    @Schema(description = "썸네일 이미지", example = "https://~~~~~~")
    private String thumbnailImageUrl;

    @Schema(description = "방문일", example = "2024-05-30")
    private LocalDate visitedAt;

    @Schema(description = "paper Id", example = "12345678")
    private Long paperId;

    public PaperListResponse(Paper paper) {
        this.title = paper.getTitle();
        this.thumbnailImageUrl = paper.getThumbnailImageUrl();
        this.visitedAt = paper.getVisitedAt();
        this.paperId = paper.getId();
    }
}

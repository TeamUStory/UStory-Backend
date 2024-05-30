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

    // TODO : 와이어 프레임에는 월-일 까지만 나와있다.
    @Schema(description = "방문일", example = "2024-05-30")
    private LocalDate visitedAt;

    @Schema(description = "다이어리 이름", example = "꽁냥껑냥")
    private String diaryName;

    @Schema(description = "상호명", example = "우규")
    private String store;

    @Schema(description = "paper Id", example = "12345678")
    private Long paperId;

    // TODO: 다이어리 엔티티 연동 후 주석 풀어야 함
    public PaperListResponse(Paper paper) {
        this.title = paper.getTitle();
        this.thumbnailImageUrl = paper.getThumbnailImageUrl();
        this.visitedAt = paper.getVisitedAt();
        this.diaryName = "다이어리 이름"; //page.getDiary().getTitle();
        this.store = paper.getAddress().getStore();
        this.paperId = paper.getId();
    }
}

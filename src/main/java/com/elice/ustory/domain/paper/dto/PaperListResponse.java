package com.elice.ustory.domain.paper.dto;

import com.elice.ustory.domain.paper.entity.Paper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class PaperListResponse {

    @Schema(name = "타이틀")
    private String title;

    @Schema(name = "썸네일 이미지")
    private String thumbnailImage;

    @Schema(name = "다이어리 이름")
    private String diaryName;

    @Schema(name = "상호명")
    private String store;

    @Schema(name = "paper Id")
    private Long paperId;

    // TODO: 다이어리 엔티티 연동 후 주석 풀어야 함
    public PaperListResponse(Paper paper) {
        this.title = paper.getTitle();
        this.thumbnailImage = paper.getThumbnailImage();
        this.diaryName = "다이어리 이름"; //page.getDiary().getTitle();
        this.store = paper.getAddress().getStore();
        this.paperId = paper.getId();
    }
}

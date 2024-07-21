package com.elice.ustory.domain.recommand.dto;

import com.elice.ustory.domain.paper.entity.Paper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendPaperResponse {

    @Schema(description = "상호명", example = "진양면옥")
    private String paperTitle;

    @Schema(description = "좋아요 수", example = "1")
    private int countGreat;

    @Schema(description = "썸네일 이미지", example = "https://~~~~~~")
    private String imgUrl;

    @Schema(description = "페이퍼 이미지", example = "1")
    private long paperId;

    public RecommendPaperResponse(Paper paper) {
        this.paperTitle = paper.getTitle();
        this.imgUrl = paper.getThumbnailImageUrl();
        this.paperId = paper.getId();
    }
}

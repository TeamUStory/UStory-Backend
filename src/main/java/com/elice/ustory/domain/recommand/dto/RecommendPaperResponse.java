package com.elice.ustory.domain.recommand.dto;

import com.elice.ustory.domain.paper.entity.Paper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendPaperResponse {

    private String paperTitle;
    private int countGrate;
    private String imgUrl;
    private long paperId;

    public RecommendPaperResponse(Paper paper) {
        this.paperTitle = paper.getTitle();
        this.imgUrl = paper.getThumbnailImageUrl();
        this.paperId = paper.getId();
    }
}

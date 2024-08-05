package com.elice.ustory.domain.recommand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MainRecommendResponse {

    @Schema(description = "상호명", example = "진양면옥")
    private String store;

    @Schema(description = "썸네일 이미지", example = "https://~~~~~~")
    private String imgUrl;

    @Schema(description = "추천 페이퍼들을 묶어놓은 키", example = "RecommendPaper1")
    private String RecommendPaperKey;

}

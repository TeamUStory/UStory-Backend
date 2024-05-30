package com.elice.ustory.domain.paper.dto;

import com.elice.ustory.domain.paper.entity.Paper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class DiaryNamePaperListResponse extends PaperListResponse {

    @Schema(description = "다이어리 이름", example = "꽁냥껑냥")
    private String diaryName;

    public DiaryNamePaperListResponse(Paper paper) {
        super(paper);
        this.diaryName = paper.getDiary().getName();
    }

}

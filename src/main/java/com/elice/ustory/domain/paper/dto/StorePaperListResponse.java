package com.elice.ustory.domain.paper.dto;

import com.elice.ustory.domain.paper.entity.Paper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class StorePaperListResponse extends PaperListResponse {

    @Schema(description = "상호명", example = "우규")
    private String store;

    public StorePaperListResponse(Paper paper) {
        super(paper);
        this.store = paper.getAddress().getStore();
    }
}

package com.elice.ustory.domain.grate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class GrateResponse {

    @Schema(description = "좋아요 여부",
            example = "0인 경우 좋아요로 지정되지 않았음을 의미 <br>" +
                    "1인 경우 좋아요로 지정되어 있음을 의미")
    private int grated;

    public GrateResponse(Boolean grated) {
        if (grated) {
            this.grated = 1;
            return;
        }

        this.grated = 0;
    }
}

package com.elice.ustory.domain.great.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class GreatResponse {

    @Schema(description = "좋아요 여부",
            example = "0인 경우 좋아요로 지정되지 않았음을 의미 <br>" +
                    "1인 경우 좋아요로 지정되어 있음을 의미")
    private int greatd;

    public GreatResponse(Boolean greatd) {
        if (greatd) {
            this.greatd = 1;
            return;
        }

        this.greatd = 0;
    }
}

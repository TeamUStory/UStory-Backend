package com.elice.ustory.domain.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExitResponse {

    @Schema(description = "나가기 성공 여부", example = "true")
    boolean success;

    public ExitResponse(boolean success) {
        this.success = success;
    }
}

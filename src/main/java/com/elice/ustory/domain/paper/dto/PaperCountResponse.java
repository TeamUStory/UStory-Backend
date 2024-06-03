package com.elice.ustory.domain.paper.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PaperCountResponse {
    @Schema(description = "Paper 개수", example = "3")
    private int count;
}

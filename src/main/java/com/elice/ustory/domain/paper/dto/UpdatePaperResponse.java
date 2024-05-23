package com.elice.ustory.domain.paper.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdatePaperResponse {
    @Schema(name = "Paper Id")
    private Long paperId;
}

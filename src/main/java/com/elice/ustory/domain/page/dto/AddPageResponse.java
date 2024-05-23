package com.elice.ustory.domain.page.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddPageResponse {
    @Schema(description = "페이지 Id", example = "12345678")
    private Long pageId;
}

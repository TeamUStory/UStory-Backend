package com.elice.ustory.domain.page.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdatePageResponse {
    @Schema(name = "페이지 Id")
    private Long pageId;
}

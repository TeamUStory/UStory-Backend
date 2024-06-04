package com.elice.ustory.domain.bookmark.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddBookmarkRequest {

    @Schema(description = "Paper Id", example = "12345678")
    private Long paperId;

}

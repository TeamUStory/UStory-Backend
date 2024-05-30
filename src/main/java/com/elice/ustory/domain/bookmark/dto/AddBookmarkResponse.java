package com.elice.ustory.domain.bookmark.dto;

import com.elice.ustory.domain.bookmark.entity.Bookmark;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class AddBookmarkResponse {

    @Schema(description = "paper Id", example = "12345678")
    private Long paperId;

    public AddBookmarkResponse(Bookmark bookmark) {
        this.paperId = bookmark.getPaper().getId();
    }
}

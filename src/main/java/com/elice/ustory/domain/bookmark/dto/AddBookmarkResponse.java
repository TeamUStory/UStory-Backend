package com.elice.ustory.domain.bookmark.dto;

import com.elice.ustory.domain.bookmark.entity.Bookmark;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class AddBookmarkResponse {

    @Schema(description = "북마크 Id", example = "12345678")
    private Long bookmarkId;

    public AddBookmarkResponse(Bookmark bookmark) {
        this.bookmarkId = bookmark.getId();
    }
}

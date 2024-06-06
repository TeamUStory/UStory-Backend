package com.elice.ustory.domain.bookmark.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class BookmarkResponse {

    @Schema(description = "북마크 여부",
            example = "0인 경우 북마크로 지정되지 않았음을 의미 <br>" +
                    "1인 경우 북마크로 지정되어 있음을 의미")
    private int bookmarked;

    public BookmarkResponse(Boolean bookmarked) {
        if (bookmarked) {
            this.bookmarked = 1;
            return;
        }

        this.bookmarked = 0;
    }
}

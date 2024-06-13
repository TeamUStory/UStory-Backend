package com.elice.ustory.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentResponse {

    @Schema(description = "Comment Id", example = "123456")
    private Long id;

    @Schema(description = "댓글", example = "다음번에는 안올듯 ㅋㅋ")
    private String content;
}

package com.elice.ustory.domain.comment.dto;

import com.elice.ustory.domain.comment.entity.Comment;
import com.elice.ustory.domain.paper.dto.LocalDateConverter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class CommentResponse {

    @Schema(description = "Comment Id", example = "123456")
    private Long id;

    @Schema(description = "댓글", example = "댓글입니다.")
    private String content;

//    private Long userId;

    @Schema(description = "댓글 작성자 닉네임", example = "피카츄")
    private String userNickname;

    @Schema(description = "댓글 작성자 프로필 이미지", example = "https://~~~.png")
    private String profileImg;

    @JsonSerialize(using = LocalDateConverter.class)
    private LocalDate createdAt;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
//        this.userId = comment.getUser().getId();
        this.userNickname = comment.getUser().getNickname();
        this.profileImg = comment.getUser().getProfileImgUrl();
        this.createdAt = LocalDate.now();
    }
}

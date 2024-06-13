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
public class CommentListResponse {

    @Schema(description = "Comment Id", example = "123456")
    private Long id;

    @Schema(description = "댓글", example = "다음엔 안가야지 ㅋㅋ")
    private String content;

//    @Schema(description = "User Id", example = "123456")
//    private Long userId;

    @Schema(description = "댓글 작성자 닉네임", example = "피카츄")
    private String userNickname;

    @Schema(description = "댓글 작성자 프로필 이미지", example = "https://~~~.png")
    private String profileImg;

    @Schema(description = "댓글 수정 및 삭제 가능 여부", example = "1")
    private int isUpdatable;

    @JsonSerialize(using = LocalDateConverter.class)
    @Schema(description = "댓글 작성 날짜", example = "2024.06.13 (목)")
    private LocalDate createdAt;

    public CommentListResponse(Comment comment, Long userId){
        this.id = comment.getId();
        this.content = comment.getContent();
//        this.userId = comment.getUser().getId();
        this.userNickname = comment.getUser().getNickname();
        this.profileImg = comment.getUser().getProfileImgUrl();
        this.createdAt = LocalDate.now();
        this.isUpdatable = comment.getUser().getId() == userId ? 1 : 0;
    }
}

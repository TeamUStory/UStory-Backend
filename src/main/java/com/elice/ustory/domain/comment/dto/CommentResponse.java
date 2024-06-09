package com.elice.ustory.domain.comment.dto;

import com.elice.ustory.domain.comment.entity.Comment;
import com.elice.ustory.domain.paper.dto.LocalDateConverter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class CommentResponse {
    private Long id;
    private String content;
    private Long userId;
    private String userNickname;
    private String profileImg;
    private int isUpdatable;

    @JsonSerialize(using = LocalDateConverter.class)
    private LocalDate createdAt;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.userId = comment.getUser().getId();
        this.userNickname = comment.getUser().getNickname();
        this.profileImg = comment.getUser().getProfileImgUrl();
        this.isUpdatable = comment.getIsUpdatable();
        this.createdAt = LocalDate.now();
    }
}

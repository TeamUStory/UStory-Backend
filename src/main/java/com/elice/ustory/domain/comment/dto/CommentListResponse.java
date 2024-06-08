package com.elice.ustory.domain.comment.dto;

import com.elice.ustory.domain.comment.entity.Comment;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CommentListResponse {
    private Long id;
    private String content;
    private Long userId;

    public CommentListResponse(Comment comment){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.userId = comment.getUser().getId();
    }
}

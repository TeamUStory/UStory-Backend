package com.elice.ustory.domain.comment.dto;

import com.elice.ustory.domain.comment.entity.Comment;
import com.elice.ustory.domain.user.entity.Users;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CommentResponse {
    private Long id;
    private String content;
    private Users user;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.user = comment.getUser();
    }
}

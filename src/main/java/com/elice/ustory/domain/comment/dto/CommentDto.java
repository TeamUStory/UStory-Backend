package com.elice.ustory.domain.comment.dto;

import com.elice.ustory.domain.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String content;

    public Comment toCommentEntity(){
        return Comment.addCommentBuilder()
                .content(this.content)
                .build();
    }
}

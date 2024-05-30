package com.elice.ustory.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCommentResponse {
    private Long id;
    private Long pageId;
    private Long userId;
    private LocalDateTime createdAt;
}

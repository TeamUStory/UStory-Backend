package com.elice.ustory.domain.comment.controller;

import com.elice.ustory.domain.comment.dto.*;
import com.elice.ustory.domain.comment.entity.Comment;
import com.elice.ustory.domain.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag(name = "comment", description = "Comment API")
@RestController
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "Get Comment API", description = "댓글 ID를 통해 불러옴.")
    @GetMapping("/paper/{paperId}/comment/{id}")  // 시험용이라 uri 더러운건 무시하셔도 됩니다.
    public ResponseEntity<CommentResponse> getComment(@PathVariable Long paperId, @PathVariable Long id) {
        Optional<Comment> comment = commentService.getComment(paperId, id);
        CommentResponse commentResponse = new CommentResponse(comment.orElseGet(Comment::new));
        return ResponseEntity.ok().body(commentResponse);
    }

    @Operation(summary = "Get Comments API", description = "모든 댓글들을 불러옴")
    @GetMapping("/paper/{paperId}")   // 시험용이라 uri 더러운건 무시하셔도 됩니다.
    public ResponseEntity<List<CommentListResponse>> getComments(@PathVariable Long paperId) {
        List<Comment> comments = commentService.getComments(paperId);
        List<CommentListResponse> commentListResponses = comments.stream()
                .map(CommentListResponse::new)
                .toList();
        return ResponseEntity.ok().body(commentListResponses);
    }

    @Operation(summary = "Post Comment API", description = "댓글을 생성함")
    @PostMapping
    public ResponseEntity<AddCommentResponse> createComment(@RequestParam Long paperId,
                                                            @RequestParam Long userId,
                                                            @RequestBody AddCommentRequest addCommentRequest) {
        Comment comment = commentService.addComment(addCommentRequest, paperId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AddCommentResponse(comment.getId(), paperId, userId, comment.getCreatedAt()));
    }

    @Operation(summary = "Update Comment API", description = "댓글을 수정함")
    @PutMapping("/{id}")
    public ResponseEntity<UpdateCommentResponse> updateComment(@PathVariable Long id, @RequestBody UpdateCommentRequest updateCommentRequest) {
        Comment updatedComment = commentService.updateComment(id, updateCommentRequest);
        return ResponseEntity.ok().body(new UpdateCommentResponse(id, updatedComment.getContent()));
    }

    @Operation(summary = "Delete Comment API", description = "댓글을 삭제함")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}

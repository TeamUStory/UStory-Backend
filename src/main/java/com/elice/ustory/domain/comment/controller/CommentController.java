package com.elice.ustory.domain.comment.controller;

import com.elice.ustory.domain.comment.dto.CommentDto;
import com.elice.ustory.domain.comment.entity.Comment;
import com.elice.ustory.domain.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getComment(@PathVariable Long id) {
        Optional<Comment> comment = commentService.getComment(id);
        return comment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get Comments API", description = "모든 댓글들을 불러옴")
    @GetMapping
    public ResponseEntity<List<Comment>> getComments() {
        List<Comment> comments = commentService.getComments();
        return ResponseEntity.ok().body(comments);
    }

    @Operation(summary = "Post Comment API", description = "댓글을 생성함")
    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto) {
        return ResponseEntity.ok().body(commentService.addComment(commentDto));
    }

    @Operation(summary = "Update Comment API", description = "댓글을 수정함")
    @PutMapping
    public ResponseEntity<CommentDto> updateComment(@RequestBody CommentDto commentDto) {
        return ResponseEntity.ok().body(commentService.updateComment(commentDto));
    }

    @Operation(summary = "Delete Comment API", description = "댓글을 삭제함")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}

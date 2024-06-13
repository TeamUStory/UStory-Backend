package com.elice.ustory.domain.comment.controller;

import com.elice.ustory.domain.comment.dto.*;
import com.elice.ustory.domain.comment.entity.Comment;
import com.elice.ustory.domain.comment.service.CommentService;
import com.elice.ustory.global.exception.dto.ErrorResponse;
import com.elice.ustory.global.jwt.JwtAuthorization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Comment API")
@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "Get Comment API", description = "댓글 ID를 통해 불러옴.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/paper/{paperId}/comment/{commentId}")  // 시험용이라 uri 더러운건 무시하셔도 됩니다.
    public ResponseEntity<CommentResponse> getComment(@PathVariable Long paperId,
                                                      @PathVariable Long commentId) {
        Optional<Comment> comment = commentService.getComment(paperId, commentId);
        CommentResponse commentResponse = new CommentResponse(comment.orElseGet(Comment::new));
        return ResponseEntity.ok().body(commentResponse);
    }

    @Operation(summary = "Get Comments API", description = "모든 댓글들을 불러옴 <br> 댓글이 존재하지 않는 경우 빈리스트를 반환한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommentListResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/paper/{paperId}")   // 시험용이라 uri 더러운건 무시하셔도 됩니다.
    public ResponseEntity<List<CommentListResponse>> getComments(@PathVariable Long paperId, @JwtAuthorization Long userId) {
        List<Comment> comments = commentService.getComments(paperId, userId);

        List<CommentListResponse> response = comments.stream()
                .map(comment -> new CommentListResponse(comment, userId))
                .toList();

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Post Comment API", description = "댓글을 생성함")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AddCommentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<AddCommentResponse> createComment(@RequestParam Long paperId,
                                                            @JwtAuthorization Long userId,
                                                            @RequestBody AddCommentRequest addCommentRequest) {
        Comment comment = commentService.addComment(addCommentRequest, paperId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AddCommentResponse(comment.getId()));
    }

    @Operation(summary = "Update Comment API", description = "댓글을 수정함")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateCommentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<UpdateCommentResponse> updateComment(@PathVariable Long commentId,
                                                               @RequestBody UpdateCommentRequest updateCommentRequest) {
        Comment updatedComment = commentService.updateComment(commentId, updateCommentRequest);
        return ResponseEntity.ok().body(new UpdateCommentResponse(commentId, updatedComment.getContent()));
    }

    @Operation(summary = "Delete Comment API", description = "댓글을 삭제함")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}

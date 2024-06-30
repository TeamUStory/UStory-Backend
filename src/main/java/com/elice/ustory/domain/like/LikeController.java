package com.elice.ustory.domain.like;

import com.elice.ustory.domain.like.dto.LikeListResponse;
import com.elice.ustory.domain.like.dto.LikeResponse;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.global.exception.dto.ErrorResponse;
import com.elice.ustory.global.exception.model.ValidationException;
import com.elice.ustory.global.jwt.JwtAuthorization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Like API")
@RestController
@RequestMapping("/papers")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "Create Like API", description = "좋아요로 지정한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{paperId}/like")
    public ResponseEntity<Void> saveLike(@PathVariable Long paperId,
                                         @JwtAuthorization Long userId) {

        likeService.saveLike(userId, paperId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Read Papers Liked API", description = "좋아요로 지정된 Paper 리스트를 불러온다. <br> 좋아요가 존재하지 않는 경우 빈리스트를 반환한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LikeListResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/likes")
    public ResponseEntity<List<LikeListResponse>> getLikedPapersByUserId(
            @JwtAuthorization Long userId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        if (page < 1) {
            throw new ValidationException("페이지는 1 이상이어야 합니다.");
        } else if (size < 1){
            throw new ValidationException("사이즈는 1 이상이어야 합니다.");
        }

        List<Paper> papers = likeService.getLikesByUser(userId, page, size);

        List<LikeListResponse> result = papers.stream()
                .map(LikeListResponse::new)
                .toList();

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Like Check API",
            description = "좋아요가 지정되어있는지 확인한다. <br>" +
                    "isLiked가 0인 경우 좋아요로 지정되지 않았음을 의미한다. <br>" +
                    "isLiked가 1인 경우 좋아요로 지정되어 있음을 의미한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LikeResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{paperId}/like")
    public ResponseEntity<LikeResponse> isPaperLiked(@PathVariable Long paperId,
                                                     @JwtAuthorization Long userId) {

        boolean isLiked = likeService.isPaperLikedByUser(userId, paperId);

        return ResponseEntity.ok(new LikeResponse(isLiked));
    }

    @Operation(summary = "Delete Like API", description = "좋아요를 해제한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{paperId}/like")
    public ResponseEntity<Void> deleteLike(@PathVariable Long paperId,
                                           @JwtAuthorization Long userId) {

        likeService.deleteLike(userId, paperId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

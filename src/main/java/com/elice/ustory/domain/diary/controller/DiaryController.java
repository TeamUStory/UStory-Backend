package com.elice.ustory.domain.diary.controller;

import com.elice.ustory.domain.diary.dto.*;
import com.elice.ustory.domain.diary.entity.DiaryCategory;
import com.elice.ustory.domain.diary.service.DiaryService;
import com.elice.ustory.global.exception.dto.ErrorResponse;
import com.elice.ustory.global.jwt.JwtAuthorization;
import com.elice.ustory.global.Validation.PageableValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Diary API")
@RestController
@RequestMapping("/diary")
@RequiredArgsConstructor
public class DiaryController {

    private final PageableValidation pageableValidation;
    private final DiaryService diaryService;

    @Operation(summary = "Create Diary API", description = "다이어리 생성 및 링크 테이블에 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AddDiaryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<AddDiaryResponse> createDiary(@JwtAuthorization Long userId,
                                                     @Valid @RequestBody DiaryDto diaryDto) {
        AddDiaryResponse addDiaryResponse = diaryService.createDiary(userId, diaryDto.toDiary(), diaryDto.getUsers());
        if (addDiaryResponse == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(addDiaryResponse);
    }

    @Operation(summary = "Get User's Diary By User API", description = "유저가 속한 다이어리 목록 불러오기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DiaryListResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<DiaryListResponse>> getDiaries(
            @JwtAuthorization Long userId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "diaryCategory", required = false) DiaryCategory diaryCategory,
            @RequestParam(name = "requestTime") LocalDateTime requestTime,
            @RequestParam(name = "searchWord", required = false) String searchWord) {

        Pageable pageable = pageableValidation.madePageable(page, size);

        List<DiaryListResponse> userDiaries = diaryService.getUserDiaries(userId, pageable, diaryCategory, requestTime, searchWord);

        return ResponseEntity.ok(userDiaries);
    }

    @Operation(summary = "Get DiaryList limit 6", description = "홈 페이지 용 최신 다이어리 6개 불러오기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DiaryListResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/home")
    public ResponseEntity<List<DiaryListResponse>> getDiaryList(@JwtAuthorization Long userId) {
        List<DiaryListResponse> result = diaryService.getUserDiaryList(userId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update Diary", description = "다이어리 정보 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AddDiaryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{diaryId}")
    public ResponseEntity<AddDiaryResponse> updateDiary(@JwtAuthorization Long userId,
                                                     @PathVariable("diaryId") Long diaryId,
                                                     @Valid @RequestBody DiaryDto diaryDto) {
        if (diaryId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        AddDiaryResponse addDiaryResponse = diaryService.updateDiary(userId, diaryId, diaryDto.toDiary(), diaryDto.getUsers());

        return ResponseEntity.ok(addDiaryResponse);
    }

    @Operation(summary = "Get Diary By DiaryId", description = "다이어리 상세 페이지 불러오기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DiaryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{diaryId}")
    public ResponseEntity<DiaryResponse> getDiaryByID(@JwtAuthorization Long userId,
                                                      @PathVariable("diaryId") Long diaryId) {
        DiaryResponse diaryResponse = diaryService.getDiaryDetailById(userId,diaryId);
        if (diaryResponse == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.ok(diaryResponse);
    }

    @Operation(summary = "Get Diary Count", description = "유저가 속한 다이어리 개수 불러오기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/count")
    public ResponseEntity<Long> getDiaryCount(@JwtAuthorization Long userId) {
        Long count = diaryService.getDiaryCount(userId);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Delete Diary", description = "다이어리 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Void> deleteDiary(@PathVariable("diaryId") Long diaryId) {
        // 모든 사람들이 다이어리에서 나갔을 때 어떻게 처리할 지
        diaryService.deleteDiary(diaryId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Exit Diary", description = "다이어리 나가기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExitResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{diaryId}/exit")
    public ResponseEntity<ExitResponse> exitDiary(@JwtAuthorization Long userId,
                                          @PathVariable("diaryId") Long diaryId) {
        return ResponseEntity.ok(diaryService.exitDiary(userId, diaryId));
    }
}

package com.elice.ustory.domain.diary.controller;

import com.elice.ustory.domain.diary.dto.DiaryDto;
import com.elice.ustory.domain.diary.dto.DiaryListResponse;
import com.elice.ustory.domain.diary.dto.DiaryResponse;
import com.elice.ustory.domain.diary.entity.DiaryCategory;
import com.elice.ustory.domain.diary.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Tag(name = "Diary API")
@RestController
@RequestMapping("/diary")
@RequiredArgsConstructor
public class DiaryController {
    private final DiaryService diaryService;

    @Operation(summary = "Create Diary API", description = "다이어리 생성 및 링크 테이블에 등록")
    @PostMapping
    public ResponseEntity<DiaryResponse> createDiary(Long userId, @Valid @RequestBody DiaryDto diaryDto) {
        DiaryResponse diary = diaryService.createDiary(userId, diaryDto.toDiary(), diaryDto.getUsers());
        if (diary == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(diary);
    }

    @Operation(summary = "Get User's Diary By User API", description = "유저가 속한 다이어리 목록 불러오기")
    @GetMapping
    public ResponseEntity<List<DiaryListResponse>> getDiaries(Long userId
            , @RequestParam(name = "page", defaultValue = "1") int page
            , @RequestParam(name = "size", defaultValue = "10") int size
            , @RequestParam(name = "diaryCategory", required = false) DiaryCategory diaryCategory
            , @RequestParam(name = "dateTime") LocalDateTime dateTime) {
        Pageable pageable = PageRequest.of(page - 1, size);

        List<DiaryListResponse> userDiaries = diaryService.getUserDiaries(userId, pageable, diaryCategory, dateTime);

        return ResponseEntity.ok(userDiaries);
    }

    @Operation(summary = "Get DiaryList limit 6", description = "홈 페이지 용 최신 다이어리 6개 불러오기")
    @GetMapping("/home")
    public ResponseEntity<List<DiaryListResponse>> getDiaryList(@RequestParam("userId") Long userId) {
        List<DiaryListResponse> result = diaryService.getUserDiaryList(userId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update Diary", description = "다이어리 정보 변경")
    @PutMapping("/{diaryId}")
    public ResponseEntity<DiaryResponse> updateDiary(Long userId, @PathVariable("diaryId") Long diaryId, @Valid @RequestBody DiaryDto diaryDto) {
        if (diaryId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        DiaryResponse diaryResponse = diaryService.updateDiary(userId, diaryId, diaryDto.toDiary(), diaryDto.getUsers());
        // TODO : 인원 수 변동 확인

        return ResponseEntity.ok(diaryResponse);
    }

    @Operation(summary = "Get Diary By DiaryId", description = "다이어리 상세 페이지 불러오기")
    @GetMapping("/{diaryId}")
    public ResponseEntity<DiaryResponse> getDiaryByID(@PathVariable("diaryId") Long diaryId) {
        if (diaryId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        DiaryResponse diary = diaryService.getDiaryDetailById(diaryId);
        if (diary == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.ok(diary);
    }

    @Operation(summary = "Get Diary Count", description = "유저가 속한 다이어리 개수 불러오기")
    @GetMapping("/count")
    public ResponseEntity<Long> getDiaryCount(@RequestParam("userId") Long userId) {
        Long count = diaryService.getDiaryCount(userId);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Delete Diary", description = "다이어리 삭제")
    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Void> deleteDiary(@PathVariable("diaryId") Long diaryId) {
        // 모든 사람들이 다이어리에서 나갔을 때 어떻게 처리할 지
        diaryService.deleteDiary(diaryId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Exit Diary", description = "다이어리 나가기")
    @GetMapping("/{diaryId}/exit")
    public ResponseEntity<Void> exitDiary(Long userId, @PathVariable("diaryId") Long diaryId) {
        diaryService.exitDiary(userId, diaryId);
        return ResponseEntity.noContent().build();
    }
}

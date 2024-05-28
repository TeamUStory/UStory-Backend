package com.elice.ustory.domain.diary.controller;

import com.elice.ustory.domain.diary.dto.DiaryDto;
import com.elice.ustory.domain.diary.entity.Diary;
import com.elice.ustory.domain.diary.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Diary API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DiaryController {
    private final DiaryService diaryService;

    @Operation(summary = "Create Diary API", description = "다이어리 생성 및 링크 테이블에 등록")
    @PostMapping("/diary")
    public ResponseEntity<Diary> createDiary(@RequestBody DiaryDto diaryDto) {
        Diary diary = diaryService.createDiary(diaryDto.toDiary(), diaryDto.getUsers());

        // TODO: Diary와 userId List를 링크 테이블에 저장 로직 필요


        return ResponseEntity.status(HttpStatus.CREATED).body(diary);
    }

    @Operation(summary = "Get User's Diary By User API", description = "유저가 속한 다이어리 목록 불러오기")
    @GetMapping("/diary")
    public ResponseEntity<List<Diary>> getDiaries(@RequestParam("userId") Long userId
            , @RequestParam(name = "page", defaultValue = "1") int page
            , @RequestParam(name = "size", defaultValue = "10") int size) {
        // TODO: userId를 통해 user-diary 링크 테이블의 다이어리 목록을 확인
        // TODO: 동적 쿼리를 통해 리스트의 다이어리 아이디를 통해 다이어리 리스트 반환


        return null;
    }

    @Operation(summary = "Update Diary", description = "다이어리 정보 변경")
    @PutMapping("/diary/{diaryId}")
    public ResponseEntity<Diary> updateDiary(@PathVariable("diaryId") Long diaryId, @RequestBody DiaryDto diaryDto) {
        if (diaryId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Diary diary = diaryService.updateDiary(diaryId, diaryDto.toDiary());
        return ResponseEntity.ok(diary);
    }

    @Operation(summary = "Get Diary By DiaryId", description = "다이어리 상세 페이지 불러오기")
    @GetMapping("/diary/{diaryId}")
    public ResponseEntity<Diary> getDiaryByID(@PathVariable("diaryId") Long diaryId) {
        if (diaryId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Diary diary = diaryService.getDiaryById(diaryId);
        if (diary == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.ok(diary);
    }

    @Operation(summary = "Delete Diary", description = "다이어리 삭제")
    @DeleteMapping("/diary/{diaryId}")
    public ResponseEntity<Void> deleteDiary(@PathVariable("diaryId") Long diaryId) {
        // 모든 사람들이 다이어리에서 나갔을 때 어떻게 처리할 지
        diaryService.deleteDiary(diaryId);
        return ResponseEntity.noContent().build();
    }

    // TODO : 다이어리 나가기, 초대하기
}

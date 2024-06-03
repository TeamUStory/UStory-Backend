package com.elice.ustory.domain.paper;

import com.elice.ustory.domain.paper.dto.PaperCountResponse;
import com.elice.ustory.domain.paper.dto.PaperListResponse;
import com.elice.ustory.domain.paper.dto.PaperMapListResponse;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.service.PaperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Papers API")
@RestController
@RequestMapping("/papers")
@RequiredArgsConstructor
public class PaperListController {

    private final PaperService paperService;

    @Operation(summary = "Read Papers By User API", description = "유저가 작성한 페이퍼 리스트를 불러온다.")
    @GetMapping("/user")
    public ResponseEntity<List<PaperListResponse>> getPapersByUser(@RequestParam(name = "userId") Long userId,
                                                                   @RequestParam(name = "page", defaultValue = "1") int page,
                                                                   @RequestParam(name = "size", defaultValue = "20") int size) {

        List<Paper> papers = paperService.getPapersByWriterId(userId, page, size);

        List<PaperListResponse> result = papers.stream()
                .map(PaperListResponse::new)
                .toList();

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Read Papers By Diary API", description = "다이어리에 포함된 페이퍼 리스트를 불러온다.")
    @GetMapping("/diary")
    public ResponseEntity<List<PaperListResponse>> getPapersByDiary(
            @RequestParam(name = "diaryId") Long diaryId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") LocalDate endDate
    ) {

        List<Paper> papers = paperService.getPapersByDiaryId(diaryId, page, size, startDate, endDate).stream().toList();

        List<PaperListResponse> response = papers.stream().map(PaperListResponse::new).toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Read Papers for Map API", description = "유저와 관련된 모든 리스트를 불러온다.")
    @GetMapping("/map")
    public ResponseEntity<List<PaperMapListResponse>> getPapersByUserForMap(@RequestParam(name = "userId") Long userId) {

        List<Paper> papers = paperService.getPapersByUserId(userId);

        List<PaperMapListResponse> response = papers.stream()
                .map(PaperMapListResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Count Write Paper By Specific User API", description = "특정 유저가 작성한 모든 페이퍼의 갯수를 불러온다.")
    @GetMapping("/count")
    public ResponseEntity<PaperCountResponse> countPapersByUser(@RequestParam(name = "userId") Long userId) {
        int count = paperService.countPapersByWriterId(userId);
        return ResponseEntity.ok(new PaperCountResponse(count));
    }
}

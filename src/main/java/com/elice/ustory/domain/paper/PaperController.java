package com.elice.ustory.domain.paper;

import com.elice.ustory.domain.bookmark.BookmarkService;
import com.elice.ustory.domain.paper.dto.AddPaperRequest;
import com.elice.ustory.domain.paper.dto.AddPaperResponse;
import com.elice.ustory.domain.paper.dto.PaperCountResponse;
import com.elice.ustory.domain.paper.dto.PaperListResponse;
import com.elice.ustory.domain.paper.dto.PaperMapListResponse;
import com.elice.ustory.domain.paper.dto.PaperResponse;
import com.elice.ustory.domain.paper.dto.UpdatePaperRequest;
import com.elice.ustory.domain.paper.dto.UpdatePaperResponse;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.service.PaperService;
import com.elice.ustory.global.exception.model.ValidationException;
import com.elice.ustory.global.jwt.JwtAuthorization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Paper API")
@RestController
@RequestMapping("/papers")
@RequiredArgsConstructor
public class PaperController {

    private final PaperService paperService;
    private final BookmarkService bookmarkService;

    @Operation(summary = "Create Paper API", description = "페이퍼를 생성한다.")
    @PostMapping
    public ResponseEntity<AddPaperResponse> create(@JwtAuthorization Long userId,
                                                   @Valid @RequestBody AddPaperRequest addPaperRequest) {

        Paper paper = paperService.create(userId, addPaperRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(new AddPaperResponse(paper.getId()));
    }

    @Operation(summary = "Update Paper API", description = "페이퍼를 수정한다.")
    @PutMapping("/{paperId}")
    public ResponseEntity<UpdatePaperResponse> update(@PathVariable Long paperId,
                                                      @JwtAuthorization Long userId,
                                                      @Valid @RequestBody UpdatePaperRequest updatePaperRequest) {

        Paper paper = paperService.update(userId, paperId, updatePaperRequest);

        return ResponseEntity.ok(new UpdatePaperResponse(paper.getId()));
    }

    @Operation(summary = "Read Paper API", description = "페이퍼를 불러온다.")
    @GetMapping("/{paperId}")
    public ResponseEntity<PaperResponse> getPaper(@PathVariable Long paperId,
                                                  @JwtAuthorization Long userId) {

        Paper paper = paperService.getPaperById(paperId);
        Boolean bookmarked = bookmarkService.isPaperBookmarkedByUser(userId, paperId);

        return ResponseEntity.ok(new PaperResponse(paper, bookmarked, userId));
    }

    @Operation(summary = "Delete Paper API", description = "페이퍼를 삭제한다.</br>(우선 사용되지 않을 API)</br>사용된다면 관리자 페이지에서 사용될 듯 함")
    @DeleteMapping("/{paperId}")
    public ResponseEntity<Void> delete(@PathVariable Long paperId,
                                       @JwtAuthorization Long userId) {

        // paperId에 해당하는 paper 삭제
        paperService.deleteById(userId, paperId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Read Papers By User API", description = "유저가 작성한 페이퍼 리스트를 불러온다.")
    @GetMapping("/written")
    public ResponseEntity<List<PaperListResponse>> getPapersByUser(@JwtAuthorization Long userId,
                                                                   @RequestParam(name = "page", defaultValue = "1") int page,
                                                                   @RequestParam(name = "size", defaultValue = "20") int size,
                                                                   @RequestParam(name = "requestTime") LocalDateTime requestTime) {

        if (page < 1) {
            throw new ValidationException("페이지는 1 이상이어야 합니다.");
        } else if (size < 1){
            throw new ValidationException("사이즈는 1 이상이어야합니다.");
        }

        List<Paper> papers = paperService.getPapersByWriterId(userId, page, size, requestTime);

        List<PaperListResponse> result = papers.stream()
                .map(PaperListResponse::new)
                .toList();

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Read Papers By Diary API", description = "다이어리에 포함된 페이퍼 리스트를 불러온다.")
    @GetMapping("/diary/{diaryId}")
    public ResponseEntity<List<PaperListResponse>> getPapersByDiary(
            @PathVariable Long diaryId,
            @RequestParam(name = "requestTime") LocalDateTime requestTime,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") LocalDate endDate
    ) {

        if (page < 1) {
            throw new ValidationException("페이지는 1 이상이어야 합니다.");
        } else if (size < 1){
            throw new ValidationException("사이즈는 1 이상이어야합니다.");
        }

        List<Paper> papers = paperService.getPapersByDiaryId(diaryId, page, size, startDate, endDate, requestTime);

        List<PaperListResponse> response = papers.stream()
                .map(PaperListResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Read Papers for Map API", description = "유저와 관련된 모든 리스트를 불러온다.")
    @GetMapping("/map")
    public ResponseEntity<List<PaperMapListResponse>> getPapersByUserForMap(@JwtAuthorization Long userId) {

        List<Paper> papers = paperService.getPapersByUserId(userId);

        List<PaperMapListResponse> response = papers.stream()
                .map(PaperMapListResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Count Write Paper By Specific User API", description = "특정 유저가 작성한 모든 페이퍼의 갯수를 불러온다.")
    @GetMapping("/count")
    public ResponseEntity<PaperCountResponse> countPapersByUser(@JwtAuthorization Long userId) {
        int count = paperService.countPapersByWriterId(userId);
        return ResponseEntity.ok(new PaperCountResponse(count));
    }

}

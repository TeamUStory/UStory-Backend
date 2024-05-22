package com.elice.ustory.domain.page;

import com.elice.ustory.domain.page.dto.PageRequest;
import com.elice.ustory.domain.page.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

@Tag(name = "Page API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PageController {

    private final PageService pageService;

    // TODO: 작성자는 어디에서 받을 것인가...
    @Operation(summary = "Create Page API", description = "페이지를 생성한다.")
    @PostMapping("/page")
    public ResponseEntity<PageResponse> create(@RequestBody PageRequest pageRequest) {

        return ResponseEntity.status(HttpStatus.CREATED).body(new PageResponse());
    }

    // TODO: 작성자는 어디에서 받을 것인가...
    @Operation(summary = "Update Page API", description = "페이지를 수정한다.")
    @PutMapping("/page/{pageId}")
    public ResponseEntity<PageResponse> update(@PathVariable Long pageId,
                                               @RequestBody PageRequest pageRequest) {

        return ResponseEntity.ok(new PageResponse());
    }

    @Operation(summary = "Read Page API", description = "페이지를 불러온다.")
    @GetMapping("/page/{pageId}")
    public ResponseEntity<PageResponse> read(@PathVariable Long pageId) {

        return ResponseEntity.ok(new PageResponse());
    }

    @Operation(summary = "Read Pages API", description = "모든 페이지를 불러온다.</br>(우선 사용되지 않을 API)</br>사용된다면 관리자 페이지에서 사용될 듯 함")
    @GetMapping("/pages")
    public ResponseEntity<List<PageResponse>> getAllPages(@RequestParam(name = "page", defaultValue = "1") int page,
                                                          @RequestParam(name = "size", defaultValue = "20") int size) {

        // 모든 page를 불러온다.

        return ResponseEntity.ok(List.of(new PageResponse()));
    }

    // TODO: userId를 임시로 작성해놨지만 변경 해야함
    @Operation(summary = "Read Pages By User API", description = "유저와 연관된 모든 페이지를 불러온다.")
    @GetMapping(value = "/pages/user", params = "userId")
    public ResponseEntity<List<PageResponse>> getAllPagesByUser(@RequestParam(name = "userId") Long userId,
                                                                @RequestParam(name = "page", defaultValue = "1") int page,
                                                                @RequestParam(name = "size", defaultValue = "20") int size) {

        // user 검증
        // user 연관된 모든 page 불러오기

        return ResponseEntity.ok(List.of(new PageResponse()));
    }

    @Operation(summary = "Read Pages By Diary API", description = "다이어리와 연관된 모든 페이지를 불러온다.")
    @GetMapping(value = "/pages/diary", params = "diaryId")
    public ResponseEntity<List<PageResponse>> getAllPagesByDiary(@RequestParam(name = "diaryId") Long diaryId,
                                                                 @RequestParam(name = "page", defaultValue = "1") int page,
                                                                 @RequestParam(name = "size", defaultValue = "20") int size) {

        // diary 검증
        // diary 연관된 모든 page 불러오기

        return ResponseEntity.ok(List.of(new PageResponse()));
    }

    @Operation(summary = "Delete Page API", description = "페이지를 삭제한다.</br>(우선 사용되지 않을 API)</br>사용된다면 관리자 페이지에서 사용될 듯 함")
    @DeleteMapping("/page/{pageId}")
    public ResponseEntity<Void> delete(@PathVariable Long pageId) {

        // pageId에 해당하는 page 삭제

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

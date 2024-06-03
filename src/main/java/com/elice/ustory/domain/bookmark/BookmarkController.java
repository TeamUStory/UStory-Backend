package com.elice.ustory.domain.bookmark;

import com.elice.ustory.domain.bookmark.dto.AddBookmarkRequest;
import com.elice.ustory.domain.bookmark.dto.AddBookmarkResponse;
import com.elice.ustory.domain.bookmark.dto.BookmarkListResponse;
import com.elice.ustory.domain.bookmark.dto.BookmarkResponse;
import com.elice.ustory.domain.bookmark.entity.Bookmark;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.service.PaperService;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Bookmark API")
@RestController
@RequestMapping("/papers")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "Create bookmark API", description = "북마크를 지정한다.")
    @PostMapping("/{paperId}/bookmark")
    public ResponseEntity<Void> saveBookmark(@RequestParam Long userId,
                                             @RequestBody AddBookmarkRequest request) {

        bookmarkService.saveBookmark(userId, request.getPaperId());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Bookmark Check API",
            description = "북마크가 되어있는지 확인한다. <br>" +
                    "isBookmarked가 0인 경우 북마크로 지정되지 않았음을 의미한다. <br>" +
                    "isBookmarked가 1인 경우 북마크로 지정되어 있음을 의미한다.")
    @GetMapping("/{paperId}/bookmark")
    public ResponseEntity<BookmarkResponse> isPaperBookmarked(@PathVariable Long paperId,
                                                              @RequestParam Long userId) {

        boolean isBookmarked = bookmarkService.isPaperBookmarkedByUser(paperId, userId);

        return ResponseEntity.ok(new BookmarkResponse(isBookmarked));
    }

    @Operation(summary = "Delete Bookmark API", description = "북마크를 해제한다.")
    @DeleteMapping("/{paperId}/bookmark")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long paperId,
                                               @RequestParam Long userId) {

        bookmarkService.deleteBookmark(userId, paperId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Read Papers Bookmarked API", description = "북마크된 Paper 리스트를 불러온다.")
    @GetMapping("/bookmark")
    public ResponseEntity<List<BookmarkListResponse>> getBookmarkedPapersByUserId(
            @RequestParam Long userId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        List<Paper> papers = bookmarkService.getBookmarksByUser(userId, page, size);

        List<BookmarkListResponse> result = papers.stream()
                .map(BookmarkListResponse::new)
                .toList();

        return ResponseEntity.ok(result);
    }

}

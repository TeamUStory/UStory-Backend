package com.elice.ustory.domain.bookmark;

import com.elice.ustory.domain.bookmark.dto.AddBookmarkResponse;
import com.elice.ustory.domain.bookmark.dto.BookmarkListResponse;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Bookmark API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final PaperService paperService;
    private final UserService userService;

    @Operation(summary = "Create bookmark API", description = "북마크를 지정한다.")
    @PostMapping("/bookmark")
    public ResponseEntity<AddBookmarkResponse> saveBookmark(@RequestParam Long userId,
                                                            @RequestParam Long paperId) {

        Users user = userService.findById(userId).orElseThrow();
        Paper paper = paperService.getPaperById(paperId);
        Bookmark bookmark = bookmarkService.saveBookmark(user, paper);

        return ResponseEntity.status(HttpStatus.CREATED).body(new AddBookmarkResponse(bookmark));
    }

    @Operation(summary = "Bookmark Check API", description = "북마크된 Paper인지 확인한다.")
    @GetMapping("/paper/{paperId}/bookmark")
    public ResponseEntity<Boolean> isPaperBookmarked(@PathVariable Long paperId,
                                                     @RequestParam Long userId) {

        boolean isBookmarked = bookmarkService.isPaperBookmarkedByUser(paperId, userId);

        return ResponseEntity.ok(isBookmarked);
    }

    @Operation(summary = "Read Papers Bookmarked API", description = "북마크된 Paper 리스트를 불러온다.")
    @GetMapping("/bookmarks")
    public ResponseEntity<List<BookmarkListResponse>> getBookmarkedPapersByUserId(@RequestParam Long userId) {

        List<Paper> papers = bookmarkService.getBookmarksByUser(userId);

        List<BookmarkListResponse> result = papers.stream()
                .map(BookmarkListResponse::new)
                .toList();

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Delete Bookmark API", description = "북마크를 해제한다.")
    @DeleteMapping("/bookmark")
    public ResponseEntity<Void> deleteBookmark(@RequestParam Long userId,
                                               @RequestParam Long paperId) {
        bookmarkService.deleteBookmark(userId, paperId);
        return ResponseEntity.noContent().build();
    }

}

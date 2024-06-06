package com.elice.ustory.domain.bookmark;

import com.elice.ustory.domain.bookmark.entity.Bookmark;
import com.elice.ustory.domain.bookmark.repository.BookmarkRepository;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.repository.PaperRepository;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
import com.elice.ustory.global.exception.model.ConflictException;
import com.elice.ustory.global.exception.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private static final String NOT_FOUND_PAPER_MESSAGE = "%d: 해당하는 페이퍼가 존재하지 않습니다.";
    private static final String NOT_FOUND_USER_MESSAGE = "%d: 해당하는 사용자가 존재하지 않습니다.";
    private static final String NOT_FOUND_BOOKMARK_MESSAGE = "해당하는 북마크가 존재하지 않습니다.";
    private static final String CONFLICT_BOOKMARK_MESSAGE = "이미 북마크로 지정되어 있습니다.";

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final PaperRepository paperRepository;

    /** 북마크 저장 */
    public Bookmark saveBookmark(Long userId, Long paperId) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_USER_MESSAGE, userId)));

        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_PAPER_MESSAGE, paperId)));

        if (isPaperBookmarkedByUser(user.getId(), paper.getId())) {
            throw new ConflictException(CONFLICT_BOOKMARK_MESSAGE);
        }

        Bookmark bookmark = new Bookmark(user, paper);
        return bookmarkRepository.save(bookmark);
    }

    /** 북마크 체크한 모든 paper 불러오기 */
    public List<Paper> getBookmarksByUser(Long userId, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        return bookmarkRepository.findPapersByUserId(userId, pageRequest);
    }

    /** 북마크 판별 메서드 */
    public boolean isPaperBookmarkedByUser(Long userId, Long paperId) {
        return bookmarkRepository.existsByUserIdAndPaperId(userId, paperId);
    }

    /** 북마크 삭제 메서드 */
    public void deleteBookmark(Long userId, Long paperId) {
        Bookmark bookmark = bookmarkRepository.findByUserIdAndPaperId(userId, paperId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_BOOKMARK_MESSAGE));

        bookmarkRepository.delete(bookmark);
    }
}

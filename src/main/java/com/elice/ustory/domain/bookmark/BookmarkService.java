package com.elice.ustory.domain.bookmark;

import com.elice.ustory.domain.bookmark.entity.Bookmark;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.global.exception.model.ConflictException;
import com.elice.ustory.global.exception.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    /** 북마크 저장 */
    public Bookmark saveBookmark(Users user, Paper paper) {

        if (isPaperBookmarkedByUser(user.getId(), paper.getId())) {
            throw new ConflictException("이미 북마크로 저장되어 있습니다.");
        }

        Bookmark bookmark = new Bookmark(user, paper);
        return bookmarkRepository.save(bookmark);
    }

    /** 북마크 체크한 모든 paper 불러오기 */
    public List<Paper> getBookmarksByUser(Long userId) {
        return bookmarkRepository.findPapersByUserId(userId);
    }

    /** 북마크 판별 메서드 */
    public boolean isPaperBookmarkedByUser(Long userId, Long paperId) {
        return bookmarkRepository.existsByUserIdAndPaperId(userId, paperId);
    }

    /** 북마크 삭제 메서드 */
    public void deleteBookmark(Long userId, Long paperId) {
        Bookmark bookmark = bookmarkRepository.findByUserIdAndPaperId(userId, paperId)
                .orElseThrow(() -> new NotFoundException("해당하는 북마크가 존재하지 않습니다."));

        bookmarkRepository.delete(bookmark);
    }
}

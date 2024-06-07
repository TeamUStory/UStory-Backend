package com.elice.ustory.domain.bookmark.repository;

import com.elice.ustory.domain.bookmark.entity.Bookmark;
import com.elice.ustory.domain.bookmark.entity.BookmarkId;
import com.elice.ustory.domain.paper.entity.Paper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, BookmarkId>, BookmarkQueryDslRepository {

    /** userId와 paperId가 일치하는 Bookmark 가져오기 */
    Optional<Bookmark> findByUserIdAndPaperId(Long userId, Long paperId);
}

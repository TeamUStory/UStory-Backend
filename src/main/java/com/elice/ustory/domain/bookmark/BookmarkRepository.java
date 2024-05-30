package com.elice.ustory.domain.bookmark;

import com.elice.ustory.domain.paper.entity.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /** userId와 paperId가 일치하는 Bookmark 가져오기 */
    Optional<Bookmark> findByUserIdAndPaperId(Long userId, Long paperId);

    /** userId와 paperId가 일치하는 Bookmark 존재 유무 확인하기 */
    boolean existsByUserIdAndPaperId(Long userId, Long paperId);

    /** userId에 해당하는 Bookmark 들의 Paper List 가져오기 */
    @Query("SELECT b.paper FROM Bookmark b WHERE b.user.id = :userId")
    List<Paper> findPapersByUserId(@Param("userId") Long userId);
}

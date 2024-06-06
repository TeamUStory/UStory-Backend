package com.elice.ustory.domain.bookmark.repository;

import com.elice.ustory.domain.paper.entity.Paper;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookmarkQueryDslRepository {

    /** userId와 paperId가 일치하는 Bookmark 존재 유무 확인하기 */
    boolean existsByUserIdAndPaperId(Long userId, Long paperId);

    /** userId에 해당하는 Bookmark 들의 Paper List 가져오기 */
    List<Paper> findPapersByUserId(Long userId, Pageable pageable);
}

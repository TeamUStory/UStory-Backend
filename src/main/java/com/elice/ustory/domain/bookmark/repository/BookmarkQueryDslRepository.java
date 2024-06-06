package com.elice.ustory.domain.bookmark.repository;

public interface BookmarkQueryDslRepository {

    /** userId와 paperId가 일치하는 Bookmark 존재 유무 확인하기 */
    boolean existsByUserIdAndPaperId(Long userId, Long paperId);


}

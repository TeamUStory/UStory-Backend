package com.elice.ustory.domain.bookmark.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.elice.ustory.domain.bookmark.entity.QBookmark.bookmark;

@Repository
@RequiredArgsConstructor
public class BookmarkRepositoryImpl implements BookmarkQueryDslRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByUserIdAndPaperId(Long userId, Long paperId) {
        return queryFactory.selectOne()
                .from(bookmark)
                .where(bookmark.user.id.eq(userId)
                        .and(bookmark.paper.id.eq(paperId)))
                .fetchFirst() != null;
    }
}

package com.elice.ustory.domain.bookmark.repository;

import com.elice.ustory.domain.paper.entity.Paper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.elice.ustory.domain.bookmark.entity.QBookmark.bookmark;

@Repository
@RequiredArgsConstructor
public class BookmarkRepositoryImpl implements BookmarkQueryDslRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByUserIdAndPaperId(Long userId, Long paperId) {
        return queryFactory.selectOne()
                .from(bookmark)
                .where(bookmark.user.id.eq(userId),
                        bookmark.paper.id.eq(paperId),
                        bookmark.paper.deletedAt.isNull())
                .fetchFirst() != null;
    }

    @Override
    public List<Paper> findPapersByUserId(Long userId, Pageable pageable) {
        return queryFactory.select(bookmark.paper)
                .from(bookmark)
                .where(bookmark.user.id.eq(userId),
                        bookmark.paper.deletedAt.isNull())
                .orderBy(bookmark.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}

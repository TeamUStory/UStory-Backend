package com.elice.ustory.domain.like.repository;

import com.elice.ustory.domain.paper.entity.Paper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.elice.ustory.domain.like.entity.QLike.like;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeQueryDslRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public boolean existsByUserIdAndPaperId(Long userId, Long paperId) {
        return queryFactory.selectOne()
                .from(like)
                .where(like.user.id.eq(userId),
                        like.paper.id.eq(paperId),
                        like.paper.deletedAt.isNull())
                .fetchFirst() != null;
    }

    @Override
    public List<Paper> findLikesByUserId(Long userId, Pageable pageable) {
        return queryFactory.select(like.paper)
                .from(like)
                .where(like.user.id.eq(userId),
                        like.paper.deletedAt.isNull())
                .orderBy(like.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}

package com.elice.ustory.domain.great.repository;

import com.elice.ustory.domain.paper.entity.Paper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.elice.ustory.domain.great.entity.QGreat.great;

@Repository
@RequiredArgsConstructor
public class GreatRepositoryImpl implements GreatQueryDslRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public boolean existsByUserIdAndPaperId(Long userId, Long paperId) {
        return queryFactory.selectOne()
                .from(great)
                .where(great.user.id.eq(userId),
                        great.paper.id.eq(paperId),
                        great.paper.deletedAt.isNull())
                .fetchFirst() != null;
    }

    @Override
    public List<Paper> findGreatsByUserId(Long userId, Pageable pageable) {
        return queryFactory.select(great.paper)
                .from(great)
                .where(great.user.id.eq(userId),
                        great.paper.deletedAt.isNull())
                .orderBy(great.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public Integer countGreatById(Long paperId) {
        return queryFactory.select(great.count().intValue())
                .from(great)
                .where(great.paper.id.eq(paperId))
                .fetchOne();
    }
}

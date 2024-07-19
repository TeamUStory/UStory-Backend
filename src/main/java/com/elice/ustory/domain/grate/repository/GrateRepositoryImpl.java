package com.elice.ustory.domain.grate.repository;

import com.elice.ustory.domain.paper.entity.Paper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.elice.ustory.domain.grate.entity.QGrate.grate;

@Repository
@RequiredArgsConstructor
public class GrateRepositoryImpl implements GrateQueryDslRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public boolean existsByUserIdAndPaperId(Long userId, Long paperId) {
        return queryFactory.selectOne()
                .from(grate)
                .where(grate.user.id.eq(userId),
                        grate.paper.id.eq(paperId),
                        grate.paper.deletedAt.isNull())
                .fetchFirst() != null;
    }

    @Override
    public List<Paper> findGratesByUserId(Long userId, Pageable pageable) {
        return queryFactory.select(grate.paper)
                .from(grate)
                .where(grate.user.id.eq(userId),
                        grate.paper.deletedAt.isNull())
                .orderBy(grate.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public Integer countGrateById(Long paperId) {
        return queryFactory.select(grate.count().intValue())
                .from(grate)
                .where(grate.paper.id.eq(paperId))
                .fetchOne();
    }
}

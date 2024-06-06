package com.elice.ustory.domain.paper.repository;

import com.elice.ustory.domain.diaryUser.entity.QDiaryUser;
import com.elice.ustory.domain.paper.entity.Paper;
import com.elice.ustory.domain.paper.entity.QPaper;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.elice.ustory.domain.paper.entity.QPaper.paper;

@Repository
@RequiredArgsConstructor
public class PaperQueryDslRepositoryImpl implements PaperQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Paper> findAllByDiaryIdAndDateRange(Long diaryId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        JPQLQuery<Paper> query = queryFactory.selectFrom(paper)
                .where(paper.diary.id.eq(diaryId),
                        startDateCondition(startDate),
                        endDateCondition(endDate))
                .orderBy(paper.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return query.fetch();
    }

    private BooleanExpression startDateCondition(LocalDate startDate) {
        return startDate != null ? paper.createdAt.goe(startDate.atStartOfDay()) : null;
    }

    private BooleanExpression endDateCondition(LocalDate endDate) {
        return endDate != null ? paper.createdAt.loe(endDate.plusDays(1).atStartOfDay().minusNanos(1)) : null;
    }

    @Override
    public List<Paper> findAllPapersByUserId(Long userId) {
        QPaper paper = QPaper.paper;
        QDiaryUser diaryUser = QDiaryUser.diaryUser;

        return queryFactory.selectFrom(paper)
                .where(paper.diary.id.in(
                        JPAExpressions.select(diaryUser.id.diary.id)
                                .from(diaryUser)
                                .where(diaryUser.id.users.id.eq(userId))
                ))
                .fetch();
    }
}

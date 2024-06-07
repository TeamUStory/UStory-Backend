package com.elice.ustory.domain.notice.repository;

import com.elice.ustory.domain.notice.dto.NoticeResponse;
import com.elice.ustory.domain.notice.entity.Notice;
import com.elice.ustory.domain.notice.entity.QNotice;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static com.elice.ustory.domain.notice.entity.QNotice.notice;


public class NoticeRepositoryImpl implements NoticeQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    public NoticeRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Notice> findAllNoticesByUserId(Long userId, LocalDateTime requestTime, Pageable pageable) {

        BooleanExpression predicate = notice.responseId.eq(userId)
                .and(notice.createdAt.loe(requestTime));

        JPQLQuery<Notice> query = queryFactory.selectFrom(notice)
                        .where(predicate)
                        .orderBy(notice.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize());
        return query.fetch();
    }

}

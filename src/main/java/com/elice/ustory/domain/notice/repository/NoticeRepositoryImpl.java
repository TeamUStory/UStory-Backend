package com.elice.ustory.domain.notice.repository;

import com.elice.ustory.domain.notice.dto.NoticeResponse;
import com.elice.ustory.domain.notice.entity.QNotice;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;


public class NoticeRepositoryImpl implements NoticeQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    public NoticeRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<NoticeResponse> findAllNoticesByUserId(Long userId, LocalDateTime requestTime, Pageable pageable) {
        QNotice notice = QNotice.notice;

        BooleanExpression predicate = notice.responseId.eq(userId)
                .and(notice.createdAt.before(requestTime));

        return queryFactory.select(Projections.constructor(NoticeResponse.class,
                        notice.message,
                        notice.createdAt,
                        notice.messageType
                ))
                .from(notice)
                .where(predicate)
                .orderBy(notice.createdAt.desc()) //최신순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

}

package com.elice.ustory.domain.diaryUser.repository;

import com.elice.ustory.domain.diary.dto.DiaryListResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.elice.ustory.domain.diaryUser.entity.QDiaryUser.diaryUser;

public class DiaryUserRepositoryImpl implements DiaryUserRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public DiaryUserRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<DiaryListResponse> searchDiary(Long userId, Pageable pageable) {
        List<DiaryListResponse> result = queryFactory
                .select(
                        Projections.constructor(
                                DiaryListResponse.class,
                                diaryUser.id.diary.id,
                                diaryUser.id.diary.name,
                                diaryUser.id.diary.imgUrl,
                                diaryUser.id.diary.diaryCategory
                        )
                )
                .from(diaryUser)
                .where(diaryUser.id.users.id.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(diaryUser.count())
                .from(diaryUser)
                .where(diaryUser.id.users.id.eq(userId));

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    @Override
    public List<DiaryListResponse> searchListDiary(Long userId) {
        return queryFactory
                .select(
                        Projections.constructor(DiaryListResponse.class,
                                diaryUser.id.diary.id,
                                diaryUser.id.diary.name,
                                diaryUser.id.diary.imgUrl,
                                diaryUser.id.diary.diaryCategory
                        )
                )
                .from(diaryUser)
                .where(diaryUser.id.users.id.eq(userId))
                .orderBy(diaryUser.id.diary.updatedAt.desc())
                .limit(6)
                .fetch();
    }

    @Override
    public Long countUserByDiary(Long diaryId) {
        return queryFactory
                .select(diaryUser.count())
                .from(diaryUser)
                .where(
                        diaryUser.id.diary.id.eq(diaryId)
                )
                .fetchOne();
    }

    @Override
    public List<String> findUserByDiary(Long diaryId) {
        return queryFactory
                .select(diaryUser.id.users.name)
                .from(diaryUser)
                .where(
                        diaryUser.id.diary.id.eq(diaryId)
                )
                .fetch();
    }

}

package com.elice.ustory.domain.diaryUser.repository;

import com.elice.ustory.domain.diary.dto.DiaryList;
import com.elice.ustory.domain.diary.entity.DiaryCategory;
import com.elice.ustory.domain.diaryUser.entity.DiaryUser;
import com.elice.ustory.domain.user.entity.Users;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static com.elice.ustory.domain.diary.entity.QDiary.diary;
import static com.elice.ustory.domain.diaryUser.entity.QDiaryUser.diaryUser;
import static com.elice.ustory.domain.friend.entity.QFriend.friend;
import static com.elice.ustory.domain.user.entity.QUsers.users;

public class DiaryUserRepositoryImpl implements DiaryUserQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    public DiaryUserRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<DiaryList> searchDiary(Long userId, Pageable pageable, DiaryCategory diaryCategory) {

        List<DiaryList> result = queryFactory
                .select(
                        Projections.constructor(
                                DiaryList.class,
                                diaryUser.id.diary.id,
                                diaryUser.id.diary.name,
                                diaryUser.id.diary.imgUrl,
                                diaryUser.id.diary.diaryCategory
                        )
                )
                .from(diaryUser)
                .where(diaryUser.id.users.id.eq(userId).and(categoryEq(diaryCategory)))
                .orderBy(diaryUser.id.diary.updatedAt.desc())
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
    public List<DiaryList> searchDiaryList(Long userId) {
        List<DiaryList> result = queryFactory
                .select(
                        Projections.constructor(DiaryList.class,
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

        return result;
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
    public Long countDiaryByUser(Long userId) {
        return queryFactory
                .select(diaryUser.count())
                .from(diaryUser)
                .where(
                        diaryUser.id.users.id.eq(userId)
                )
                .fetchOne();
    }

    @Override
    public List<String> findUserByDiary(Long diaryId) {
        return queryFactory
                .select(diaryUser.id.users.nickname)
                .from(diaryUser)
                .where(
                        diaryUser.id.diary.id.eq(diaryId)
                )
                .fetch();
    }

    @Override
    public List<Tuple> findUsersByDiary(Long userId,Long diaryId, List<String> userList){
        return queryFactory
                    .select(friend.friendUser.as(users),
                            diaryUser.id.diary.id.as(diary.id))
                    .from(friend)
                    .leftJoin(diaryUser)
                    .on(friend.friendUser.id.eq(diaryUser.id.users.id))
                    .where(
                            friend.user.id.eq(userId)
                            .and(
                                    friend.friendUser.nickname.in(userList)
                            )
                            .and(
                                    diaryUser.id.diary.id.eq(diaryId)
                                            .or(diaryUser.id.diary.id.isNull())
                            )
                    )
                    .fetch();

    }

    @Override
    public DiaryUser findDiaryUserById(Long userId, Long diaryId){
        return queryFactory
                .selectFrom(diaryUser)
                .where(
                        diaryUser.id.users.id.eq(userId)
                                .and(diaryUser.id.diary.id.eq(diaryId))
                ).fetchOne();
    }

    private BooleanExpression categoryEq(DiaryCategory diaryCategory) {
        return diaryCategory != null ? diaryUser.id.diary.diaryCategory.eq(diaryCategory) : null;
    }

}

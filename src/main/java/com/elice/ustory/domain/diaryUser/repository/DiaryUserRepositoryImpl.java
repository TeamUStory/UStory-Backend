package com.elice.ustory.domain.diaryUser.repository;

import com.elice.ustory.domain.diary.dto.DiaryFriend;
import com.elice.ustory.domain.diary.dto.DiaryList;
import com.elice.ustory.domain.diary.entity.DiaryCategory;
import com.elice.ustory.domain.diaryUser.entity.DiaryUser;
import com.elice.ustory.domain.friend.entity.FriendStatus;
import com.elice.ustory.domain.user.entity.Users;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

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
    public List<DiaryList> searchDiary(Long userId, Pageable pageable, DiaryCategory diaryCategory, LocalDateTime dateTime, String searchWord) {

        return queryFactory
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
                .where(
                        diaryUser.id.users.id.eq(userId)
                                .and(diaryUser.id.diary.createdAt.loe(dateTime))
                                .and(categoryEq(diaryCategory))
                                .and(wordGrate(searchWord))
                )
                .orderBy(diaryUser.id.diary.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<DiaryList> searchDiaryList(Long userId) {
        return queryFactory
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

    // Diary Update 시
    @Override
    public List<Tuple> findUsersByDiary(Long userId, Long diaryId, List<String> userList) {
        return queryFactory
                .select(friend.friendUser.as(users),
                        diaryUser.id.diary.id.as(diary.id))
                .from(friend)
                .leftJoin(diaryUser)
                .on(friend.friendUser.id.eq(diaryUser.id.users.id)
                        .and(diaryUser.id.diary.id.eq(diaryId)))
                .where(
                        friend.user.id.eq(userId)
                                .and(friend.status.eq(FriendStatus.ACCEPTED))
                                .and(
                                        friend.friendUser.nickname.in(userList)
                                )
                )
                .fetch();

    }

    @Override
    public DiaryUser findDiaryUserById(Long userId, Long diaryId) {
        return queryFactory
                .selectFrom(diaryUser)
                .where(
                        diaryUser.id.users.id.eq(userId)
                                .and(diaryUser.id.diary.id.eq(diaryId))
                ).fetchOne();
    }

    // Diary Create 시
    @Override
    public List<Users> findFriendUsersByList(Long userId, List<String> userList) {
        return queryFactory
                .select(friend.friendUser.as(users))
                .from(friend)
                .where( friend.user.id.eq(userId)
                    .and(friend.friendUser.nickname.in(userList))
                        .and(friend.status.eq(FriendStatus.ACCEPTED))
                )
                .fetch();
    }

    @Override
    public List<DiaryFriend> findUsersByDiaryId(Long userId,Long diaryId){
        return queryFactory
                .select(
                        Projections
                                .constructor(DiaryFriend.class,
                                        diaryUser.id.users.nickname,
                                        diaryUser.id.users.profileImgUrl)
                )
                .from(diaryUser)
                .where(diaryUser.id.diary.id.eq(diaryId))
                .fetch();
    }


    private BooleanExpression categoryEq(DiaryCategory diaryCategory) {
        return diaryCategory != null ? diaryUser.id.diary.diaryCategory.eq(diaryCategory) : null;
    }

    private BooleanExpression wordGrate(String searchWord) {
        return searchWord != null ? diaryUser.id.diary.name.contains(searchWord) : null;
    }

}

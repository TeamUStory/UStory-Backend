package com.elice.ustory.domain.friend.repository;

import com.elice.ustory.domain.friend.dto.UserFriendDTO;
import com.elice.ustory.domain.friend.dto.UserListDTO;
import com.elice.ustory.domain.friend.entity.QFriend;
import com.elice.ustory.domain.user.entity.QUsers;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;

public class FriendRepositoryImpl implements FriendRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<UserFriendDTO> findAllFriendsByUserId(Long userId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QFriend friend = QFriend.friend;
        QUsers user = QUsers.users;

        return queryFactory.select(Projections.constructor(UserFriendDTO.class,
                        friend.user.id,
                        friend.friendUser.id,
                        user.email,
                        user.name,
                        user.nickname,
                        user.profileImg,
                        friend.invitedAt,
                        friend.acceptedAt
                ))
                .from(friend)
                .join(friend.friendUser, user)
                .where(friend.user.id.eq(userId)
                        .or(friend.friendUser.id.eq(userId)))
                .fetch();
    }

    @Override
    public List<UserFriendDTO> findFriendsByUserIdAndNickname(Long userId, String nickname) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QFriend friend = QFriend.friend;
        QUsers user = QUsers.users;

        return queryFactory.select(Projections.constructor(UserFriendDTO.class,
                        friend.user.id,
                        friend.friendUser.id,
                        user.email,
                        user.name,
                        user.nickname,
                        user.profileImg,
                        friend.invitedAt,
                        friend.acceptedAt
                ))
                .from(friend)
                .join(friend.friendUser, user)
                .where((friend.user.id.eq(userId)
                        .or(friend.friendUser.id.eq(userId)))
                        .and(user.nickname.contains(nickname)))
                .fetch();
    }

    @Override
    public List<UserListDTO> findAllUsersByNickname(String nickname) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QUsers user = QUsers.users;

        return queryFactory.select(Projections.constructor(UserListDTO.class,
                        user.id,
                        user.email,
                        user.name,
                        user.nickname,
                        user.profileImg
                ))
                .from(user)
                .where(user.nickname.contains(nickname))
                .fetch();
    }

}

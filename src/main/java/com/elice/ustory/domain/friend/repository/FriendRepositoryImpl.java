package com.elice.ustory.domain.friend.repository;

import com.elice.ustory.domain.friend.dto.FriendRequestDTO;
import com.elice.ustory.domain.friend.dto.UserFriendDTO;
import com.elice.ustory.domain.friend.entity.FriendStatus;
import com.elice.ustory.domain.friend.entity.QFriend;
import com.elice.ustory.domain.user.entity.QUsers;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class FriendRepositoryImpl implements FriendQueryDslRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<UserFriendDTO> findFriends(Long userId, String nickname) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QFriend friend = QFriend.friend;
        QUsers user = QUsers.users;

        BooleanExpression predicate = friend.id.userId.eq(userId)
                .and(friend.status.eq(FriendStatus.ACCEPTED));
        if (nickname != null && !nickname.isEmpty()) {
            predicate = predicate.and(user.nickname.containsIgnoreCase(nickname));
        }

        return queryFactory.select(Projections.constructor(UserFriendDTO.class,
                        user.name,
                        user.nickname,
                        user.profileImgUrl
                ))
                .from(friend)
                .join(friend.friendUser, user)
                .where(predicate)
                .fetch();
    }

    @Override
    public List<FriendRequestDTO> findFriendRequests(Long userId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QFriend friend = QFriend.friend;
        QUsers sender = new QUsers("sender");
        QUsers receiver = new QUsers("receiver");

        return queryFactory.select(Projections.constructor(FriendRequestDTO.class,
                        friend.invitedAt,
                        sender.name,
                        sender.profileImgUrl,
                        sender.nickname,
                        receiver.nickname
                ))
                .from(friend)
                .join(friend.user, sender)
                .join(friend.friendUser, receiver)
                .where(friend.id.friendId.eq(userId)
                        .and(friend.status.eq(FriendStatus.PENDING)))
                .fetch();
    }

}
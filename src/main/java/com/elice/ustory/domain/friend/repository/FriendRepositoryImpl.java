package com.elice.ustory.domain.friend.repository;

import com.elice.ustory.domain.friend.dto.FriendRequestListDTO;
import com.elice.ustory.domain.friend.dto.UserFriendDTO;
import com.elice.ustory.domain.friend.entity.FriendStatus;
import com.elice.ustory.domain.friend.entity.QFriend;
import com.elice.ustory.domain.user.entity.QUsers;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

public class FriendRepositoryImpl implements FriendQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public FriendRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }


    @Override
    public List<UserFriendDTO> findFriends(Long userId, String nickname) {
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
    public List<FriendRequestListDTO> findFriendRequests(Long userId) {
        QFriend friend = QFriend.friend;
        QUsers sender = new QUsers("sender");

        return queryFactory.select(Projections.constructor(FriendRequestListDTO.class,
                        sender.name,
                        sender.profileImgUrl,
                        sender.nickname
                ))
                .from(friend)
                .join(friend.user, sender)
                .where(friend.id.friendId.eq(userId)
                        .and(friend.status.eq(FriendStatus.PENDING)))
                .fetch();
    }

    @Override
    public boolean existsByReceiverAndSender(Long receiverId, Long senderId) {
        QFriend friend = QFriend.friend;

        Number count = queryFactory.selectOne()
                .from(friend)
                .where(friend.id.userId.eq(receiverId)
                        .and(friend.id.friendId.eq(senderId)))
                .fetchFirst();

        return count != null && count.longValue() > 0;
    }

}
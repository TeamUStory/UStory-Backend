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
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public class FriendRepositoryImpl implements FriendQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public FriendRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }


    @Override
    public List<UserFriendDTO> findFriends(Long userId, String nickname, LocalDateTime requestTime , Pageable pageable) {
        QFriend friend = QFriend.friend;
        QUsers user = QUsers.users;

        BooleanExpression predicate = friend.id.userId.eq(userId)
                .and(friend.status.eq(FriendStatus.ACCEPTED))
                .and(friend.invitedAt.before(requestTime));

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
                .orderBy(user.nickname.asc()) // 닉네임 순으로 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<FriendRequestListDTO> findFriendRequests(Long userId, LocalDateTime requestTime, Pageable pageable) {
        QFriend friend = QFriend.friend;
        QUsers sender = new QUsers("sender");

        BooleanExpression predicate = friend.id.friendId.eq(userId)
                .and(friend.status.eq(FriendStatus.PENDING))
                .and(friend.invitedAt.before(requestTime));

        return queryFactory.select(Projections.constructor(FriendRequestListDTO.class,
                        sender.name,
                        sender.profileImgUrl,
                        sender.nickname
                ))
                .from(friend)
                .join(friend.user, sender)
                .where(predicate)
                .orderBy(friend.invitedAt.desc())  // 최근 요청 순으로 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public boolean existsBySenderAndReceiverAndStatus(Long senderId, Long receiverId, FriendStatus status) {
        QFriend friend = QFriend.friend;
        return queryFactory.selectOne()
                .from(friend)
                .where(friend.id.userId.eq(senderId)
                        .and(friend.id.friendId.eq(receiverId))
                        .and(friend.status.eq(status)))
                .fetchFirst() != null;
    }

    @Override
    public boolean existsBySenderAndReceiver(Long senderId, Long receiverId) {
        QFriend friend = QFriend.friend;
        return queryFactory.selectOne()
                .from(friend)
                .where(friend.id.userId.eq(senderId)
                        .and(friend.id.friendId.eq(receiverId)))
                .fetchFirst() != null;
    }
}
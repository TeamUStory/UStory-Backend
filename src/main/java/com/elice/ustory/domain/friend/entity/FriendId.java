package com.elice.ustory.domain.friend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;


/**
 * 복합키 유니크 유효성 체크 어노테이션
 */
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class FriendId implements Serializable {
    @Column(nullable = false, name="user_id")
    private long userId;
    @Column(nullable = false, name="friend_id")
    private long friendId;

    public FriendId(long userId, long friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }

}

package com.elice.ustory.domain.friend.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class FriendId implements Serializable {
    private long user_id;
    private long friend_id;

    public FriendId(long user_id, long friend_id) {
        this.user_id = user_id;
        this.friend_id = friend_id;
    }

}

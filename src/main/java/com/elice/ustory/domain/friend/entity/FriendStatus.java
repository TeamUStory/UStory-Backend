package com.elice.ustory.domain.friend.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FriendStatus {
    PENDING,
    ACCEPTED,
    REJECTED;
}

package com.elice.ustory.domain.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UserFriendDTO {
    private final Long userId;
    private final Long friendId;
    private final String email;
    private final String name;
    private final String nickname;
    private final String profileImg;
    private final LocalDateTime invitedAt;
    private final LocalDateTime acceptedAt;
}

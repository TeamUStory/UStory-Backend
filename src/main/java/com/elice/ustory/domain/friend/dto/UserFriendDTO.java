package com.elice.ustory.domain.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UserFriendDTO {
    private final String name;
    private final String nickname;
    private final String profileImgUrl;
}

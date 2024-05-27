package com.elice.ustory.domain.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserListDTO {
    private final Long userId;
    private final String email;
    private final String name;
    private final String nickname;
    private final String profileImg;
}

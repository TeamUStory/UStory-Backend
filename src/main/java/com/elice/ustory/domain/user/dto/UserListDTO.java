package com.elice.ustory.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserListDTO {
    private final String name;
    private final String nickname;
    private final String profileImgUrl;
}

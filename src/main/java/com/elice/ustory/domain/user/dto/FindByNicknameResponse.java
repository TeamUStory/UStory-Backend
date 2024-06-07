package com.elice.ustory.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindByNicknameResponse {
    private final Boolean isExist;
    private final String name;
    private final String nickname;
    private final String profileImgUrl;
}

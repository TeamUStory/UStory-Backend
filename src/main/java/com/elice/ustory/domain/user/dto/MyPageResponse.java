package com.elice.ustory.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MyPageResponse {
    String nickname;
    String name;
    String profileDescription;
    String profileImgUrl;
}

package com.elice.ustory.domain.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class UserFriendDTO {

    @Schema(description = "친구의 이름", example = "김땡땡")
    @NotNull
    private final String name;

    @Schema(description = "친구의 닉네임", example = "퐁퐁")
    @NotNull
    private final String nickname;

    @Schema(description = "친구의 프로필이미지", example = "https://~")
    @NotNull
    private final String profileImgUrl;

}

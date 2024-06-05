package com.elice.ustory.domain.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class FriendRequestListDTO {
    @Schema(description = "친구요청 유저의 이름", example = "홍길동")
    @NotNull
    private String name;

    @Schema(description = "친구요청 유저의 프로필이미지", example = "https://~")
    @NotNull
    private String profileImgUrl;

    @Schema(description = "친구요청 유저의 닉네임", example = "코코")
    @NotNull
    private String senderNickname;


    public FriendRequestListDTO(String name, String profileImgUrl, String senderNickname) {
        this.name = name;
        this.profileImgUrl = profileImgUrl;
        this.senderNickname = senderNickname;
    }


}
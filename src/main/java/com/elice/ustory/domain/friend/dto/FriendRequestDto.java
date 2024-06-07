package com.elice.ustory.domain.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FriendRequestDto {
    @Schema(description = "친구요청할 닉네임", example = "피카츄")
    @NotNull
    private String receiverNickname;

    public FriendRequestDto(String receiverNickname) {
        this.receiverNickname = receiverNickname;
    }

}

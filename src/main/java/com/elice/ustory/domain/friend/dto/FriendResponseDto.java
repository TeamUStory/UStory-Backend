package com.elice.ustory.domain.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FriendResponseDto {

    @Schema(description = "친구 요청을 보낸 사용자의 닉네임", example = "피카츄")
    @NotNull
    private String senderNickname;

    @Schema(description = "수락/거절 여부", example = "true")
    @NotNull
    @Pattern(regexp = "true|false", message = "accepted 값은 true 또는 false여야 합니다.")
    private boolean accepted;

    public FriendResponseDto(String senderNickname, boolean accepted) {
        this.senderNickname = senderNickname;
        this.accepted = accepted;
    }

}

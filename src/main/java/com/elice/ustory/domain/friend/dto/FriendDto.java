package com.elice.ustory.domain.friend.dto;

import com.elice.ustory.domain.friend.entity.Friend;
import com.elice.ustory.domain.friend.entity.FriendId;
import com.elice.ustory.domain.friend.entity.FriendStatus;
import com.elice.ustory.domain.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendDto {
    @NotNull
    @Schema(description = "친구요청Id", example = "1")
    private Long senderId;

    @NotNull
    @Schema(description = "친구요청Id", example = "1")
    private Long receiverId;


    public FriendDto(Long senderId, Long receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public Friend toFriend(Users sender, Users receiver) {
        return Friend.builder()
                .id(new FriendId(sender.getId(), receiver.getId()))
                .invitedAt(LocalDateTime.now())
                .status(FriendStatus.PENDING)
                .user(sender)
                .friendUser(receiver)
                .build();
    }

}

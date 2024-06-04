package com.elice.ustory.domain.friend.dto;

import com.elice.ustory.domain.friend.entity.Friend;
import com.elice.ustory.domain.friend.entity.FriendId;
import com.elice.ustory.domain.friend.entity.FriendStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.elice.ustory.domain.user.entity.Users;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class FriendRequestDTO {
    @Schema(description = "요청보낸는시간", example = "2024-06-03T18:02:59.529Z")
    private LocalDateTime invitedAt;

    @Schema(description = "로그인한 유저의 이름", example = "홍길동")
    private String name;

    @Schema(description = "로그인한 유저의 프로필이미지", example = "https://~")
    private String profileImgUrl;

    @Schema(description = "로그인한 유저의 닉네임", example = "코코")
    private String senderNickname;

    @Schema(description = "친구요청할 유저의 닉네임", example = "퐁퐁")
    private String receiverNickname;

    public FriendRequestDTO(LocalDateTime invitedAt, String name, String profileImgUrl, String senderNickname, String receiverNickname) {
        this.invitedAt = invitedAt;
        this.name = name;
        this.profileImgUrl = profileImgUrl;
        this.senderNickname = senderNickname;
        this.receiverNickname = receiverNickname;
    }

    public Friend toFriend(Users sender, Users receiver) {
        FriendId friendId = new FriendId(sender.getId(), receiver.getId());
        return Friend.builder()
                .id(friendId)
                .invitedAt(this.invitedAt != null ? this.invitedAt : LocalDateTime.now())
                .status(FriendStatus.PENDING)
                .user(sender)
                .friendUser(receiver)
                .build();
    }


}
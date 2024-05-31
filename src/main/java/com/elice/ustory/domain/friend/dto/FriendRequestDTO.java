package com.elice.ustory.domain.friend.dto;

import com.elice.ustory.domain.friend.entity.Friend;
import com.elice.ustory.domain.friend.entity.FriendId;
import com.elice.ustory.domain.friend.entity.FriendStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.elice.ustory.domain.user.entity.Users;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class FriendRequestDTO {
    private LocalDateTime invitedAt;
    private String name;
    private String profileImg;
    private String senderNickname;
    private String receiverNickname;

    public FriendRequestDTO(LocalDateTime invitedAt, String name, String profileImg, String senderNickname, String receiverNickname) {
        this.invitedAt = invitedAt;
        this.name = name;
        this.profileImg = profileImg;
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
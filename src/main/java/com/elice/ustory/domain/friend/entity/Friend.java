package com.elice.ustory.domain.friend.entity;

import com.elice.ustory.domain.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="friend")
public class Friend {
    @EmbeddedId
    private FriendId id;


    @Column(nullable = false, name="invited_at")
    private LocalDateTime invitedAt;

    @Column(name="accepted_at")
    private LocalDateTime acceptedAt;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Users user;

    @ManyToOne
    @MapsId("friendId")
    @JoinColumn(name = "friend_id", insertable = false, updatable = false)
    private Users friendUser;

    @Enumerated(EnumType.STRING)  // 열거형 값을 문자열로 저장
    @Column(nullable = false)
    private FriendStatus status;

    public void updateStatus(FriendStatus status) {
        this.status = status;
        if (status == FriendStatus.ACCEPTED) {
            this.acceptedAt = LocalDateTime.now();
        }
    }

}



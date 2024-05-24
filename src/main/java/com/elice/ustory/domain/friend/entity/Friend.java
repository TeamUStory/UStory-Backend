package com.elice.ustory.domain.friend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
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

    //:TODO Users 연결되면 @ManyToOne user id 연결하기
//    @ManyToOne
//    @MapsId("userId")
//    @JoinColumn(name = "user_id", insertable = false, updatable = false)
//    private Users user;
//
//    @ManyToOne
//    @MapsId("friendId")
//    @JoinColumn(name = "friend_id", insertable = false, updatable = false)
//    private Users friendUser;

}



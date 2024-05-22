package com.elice.ustory.domain.friend.entity;

import jakarta.persistence.EmbeddedId;
import lombok.*;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Friend {
    @EmbeddedId
    private FriendId id;

    private LocalDateTime invitationDate;
    private LocalDateTime acceptanceDate;
}


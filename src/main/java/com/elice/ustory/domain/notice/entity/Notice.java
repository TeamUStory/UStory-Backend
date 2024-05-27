package com.elice.ustory.domain.notice.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="notice")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, name = "receiver_id")
    private long receiverId;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false, name = "sender_id")
    private long senderId;

    @Enumerated(EnumType.STRING) // Enum을 문자열로 저장
    @Column(nullable = false, name = "message_type")
    private MessageType messageType;
}


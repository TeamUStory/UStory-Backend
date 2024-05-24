package com.elice.ustory.domain.notice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(nullable = false, name = "message_type")
    private MessageType messageType;
}


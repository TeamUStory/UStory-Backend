package com.elice.ustory.domain.notice.entity;

import com.elice.ustory.domain.paper.entity.Paper;
import jakarta.persistence.*;
import lombok.*;

@Getter
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

    @Column(name = "sender_id")
    private long senderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_id")
    private Paper paper;

    @Column(nullable = false, name = "message_type")
    private int messageType; // `tinyint`로 설정


    @Builder
    public Notice(Long receiverId, Long senderId, Paper paper, String message, int messageType) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.paper = paper;
        this.message = message;
        this.messageType = messageType;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    // toString 메서드 추가 (디버깅 용도)
    @Override
    public String toString() {
        return "Notice{" +
                "id=" + id +
                ", receiverId=" + receiverId +
                ", message='" + message + '\'' +
                ", senderId=" + senderId +
                ", paper=" + paper +
                ", messageType=" + messageType +
                '}';
    }

}


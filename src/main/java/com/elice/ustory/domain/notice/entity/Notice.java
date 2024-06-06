package com.elice.ustory.domain.notice.entity;

import com.elice.ustory.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name="notice")
public class Notice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, name = "response_id")
    private long responseId;

    @Column(nullable = false)
    private String message;

    @Setter
    @Column(nullable = false, name = "request_id")
    private long requestId;

    @Column(nullable = false, name = "message_type", columnDefinition = "tinyint")
    private int messageType;


    @Builder
    public Notice(Long responseId, Long requestId, String message, int messageType) {
        this.responseId = responseId;
        this.requestId = requestId;
        this.message = message;
        this.messageType = messageType;
    }

    // toString 메서드 추가 (디버깅 용도)
    @Override
    public String toString() {
        return "Notice {" +
                "id=" + id +
                ", responseId=" + responseId +
                ", message='" + message + '\'' +
                ", requestId=" + requestId +
                ", messageType=" + messageType +
                '}';
    }

}


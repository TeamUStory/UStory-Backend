package com.elice.ustory.domain.notice.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
public class NoticeRequest {

    private Long paperId;

    // 유저 닉네임 (보내는 쪽, Paper_Id, Sender_Id)
    private Long senderId;

    private Long responseId;

    private int messageType;

    @Builder
    public NoticeRequest(Long paperId, Long senderId, Long responseId, int messageType) {
        this.paperId = paperId;
        this.senderId = senderId;
        this.responseId = responseId;
        this.messageType = messageType;
    }
}

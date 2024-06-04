package com.elice.ustory.domain.notice.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class NoticeRequest {

    @Schema(description = "Paper_Id", example = "1")
    private Long paperId;

    @Schema(description = "Sender_Id", example = "1")
    private Long senderId;

    @Schema(description = "response_Id", example = "2")
    private Long responseId;

    @Schema(description = "메세지 타입", example = "1")
    private int messageType;

    @Builder
    public NoticeRequest(Long paperId, Long senderId, Long responseId, int messageType) {
        this.paperId = paperId;
        this.senderId = senderId;
        this.responseId = responseId;
        this.messageType = messageType;
    }
}

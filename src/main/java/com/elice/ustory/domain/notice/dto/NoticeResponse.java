package com.elice.ustory.domain.notice.dto;

import com.elice.ustory.domain.notice.entity.Notice;
import com.elice.ustory.global.exception.model.ValidationException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class NoticeResponse {

    @Schema(description = "메세지 타입", example = "친구")
    @NotNull
    private String type;

    @Schema(description = "메세지 내용", example = "친구 요청이 있습니다.")
    @NotNull
    private String message;

    @Schema(description = "알림 생성시간", example = "2024-06-03T18:02:59.529Z")
    @NotNull
    private LocalDateTime time;

    @Schema(description = "페이퍼Id", example = "1")
    private Long paperId;

    public NoticeResponse(Notice notice) {

        this.message = notice.getMessage();
        this.time = notice.getCreatedAt();

        // MessageType을 보고 paperId를 set해줘.
        switch (notice.getMessageType()) {
            case 2, 4 -> this.paperId = notice.getRequestId();
        }

        //MessageType을 보고 type을 지정해라.
        switch (notice.getMessageType()) {
            case 1, 3 -> this.type = "친구";
            case 2 -> this.type = "코멘트";
            case 4 -> this.type = "기록";
            default -> throw new ValidationException("잘못된 메시지 타입입니다.");
        }
    }

    public NoticeResponse(String type, String message, LocalDateTime time, Long paperId) {
        this.type = type;
        this.message = message;
        this.time = time;
        this.paperId = paperId;
    }


}

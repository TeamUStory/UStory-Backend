package com.elice.ustory.domain.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class NoticeResponse {

    @Schema(description = "메세지 타입", example = "친구")
    private String type;

    @Schema(description = "메세지 내용", example = "친구 요청이 있습니다.")
    private String message;

    @Schema(description = "알림 생성시간", example = "2024-06-03T18:02:59.529Z")
    private LocalDateTime time;

    @Schema(description = "페이퍼Id", example = "1")
    private Long paperId;
}

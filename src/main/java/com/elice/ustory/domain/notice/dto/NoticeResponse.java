package com.elice.ustory.domain.notice.dto;

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

    private String type;
    private String message;
    private LocalDateTime time;
    private Long paperId;
}

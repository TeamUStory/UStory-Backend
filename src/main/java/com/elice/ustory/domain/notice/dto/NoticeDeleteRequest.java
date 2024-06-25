package com.elice.ustory.domain.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeDeleteRequest {

    @NotEmpty(message = "림 ID 목록은 비어 있을 수 없습니다.")
    @Schema(description = "알림 ID 목록", example = "[1, 2, 3]")
    private List<Long> noticeIds;
}

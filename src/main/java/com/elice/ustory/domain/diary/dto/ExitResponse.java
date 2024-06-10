package com.elice.ustory.domain.diary.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExitResponse {
    boolean success;

    public ExitResponse(boolean success) {
        this.success = success;
    }
}

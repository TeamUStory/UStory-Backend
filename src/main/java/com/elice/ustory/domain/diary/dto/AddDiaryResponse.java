package com.elice.ustory.domain.diary.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddDiaryResponse {
    Long id;

    public AddDiaryResponse(Long id) {
        this.id = id;
    }
}

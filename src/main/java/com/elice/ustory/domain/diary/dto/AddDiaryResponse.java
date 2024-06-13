package com.elice.ustory.domain.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddDiaryResponse {

    @Schema(description = "Diary Id", example = "123456")
    Long id;

    public AddDiaryResponse(Long id) {
        this.id = id;
    }
}

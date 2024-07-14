package com.elice.ustory.domain.like.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LikeCountResponse {

    @Schema(description = "서비스 내에서 해당 페이퍼에 좋아요 총 개수",  example = "정수 타입으로, 0 부터 시작")
    private int countLike;

}

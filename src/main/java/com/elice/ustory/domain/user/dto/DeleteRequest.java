package com.elice.ustory.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class DeleteRequest {
    private Long userId; //TODO: 쿠키에서 가져오기
}

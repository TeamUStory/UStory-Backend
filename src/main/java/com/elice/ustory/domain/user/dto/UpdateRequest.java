package com.elice.ustory.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequest {
    private Long userId; //TODO: 쿠키에서 가져오기
    private String name;
    private String nickname;
    private String password;
    private String profileImgUrl;
    private String profileDescription;
}

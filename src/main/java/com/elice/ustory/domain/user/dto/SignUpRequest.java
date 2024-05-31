package com.elice.ustory.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SignUpRequest {
    private String email;
    private String name;
    private String nickname;
    private String password;
    private String profileImgUrl;
    private String profileDescription;
}

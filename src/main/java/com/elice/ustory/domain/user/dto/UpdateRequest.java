package com.elice.ustory.domain.user.dto;

import lombok.Data;

@Data
public class UpdateRequest {
    private String name;
    private String nickname;
    private String password;
    private String profileImgUrl;
    private String profileDescription;
}

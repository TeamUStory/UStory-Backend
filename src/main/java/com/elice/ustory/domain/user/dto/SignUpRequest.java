package com.elice.ustory.domain.user.dto;

import com.elice.ustory.domain.user.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    private String email;
    private Users.LoginType loginType;
    private String name;
    private String nickname;
    private String password;
    private String profileImg;
}

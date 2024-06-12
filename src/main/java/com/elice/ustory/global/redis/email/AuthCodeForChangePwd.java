package com.elice.ustory.global.redis.email;

import jakarta.persistence.Id;
import lombok.Builder;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "authCodeForChangePwd", timeToLive = 60*3L) // 인증코드 유효시간은 3분
public class AuthCodeForChangePwd {
    @Id
    private String id; // 수신자의 이메일(toEmail)을 받음

    private String authCode;

    @Builder
    public AuthCodeForChangePwd(String toEmail, String authCode) {
        this.id = toEmail;
        this.authCode = authCode;
    }
}

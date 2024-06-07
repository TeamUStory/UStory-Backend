package com.elice.ustory.global.redis.email;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor
@RedisHash(value = "authCode", timeToLive = 60*3L) // 인증코드 유효시간은 3분
public class AuthCode {
    @Id
    private String id; // 수신자의 이메일(toEmail)을 받음

    private String authCode;
    //TODO: authCode 생성 로직을 이곳으로 옮길까?

    @Builder
    public AuthCode(String toEmail, String authCode) {
        this.id = toEmail;
        this.authCode = authCode;
    }

}

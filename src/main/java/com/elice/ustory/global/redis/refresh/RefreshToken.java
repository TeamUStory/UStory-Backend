package com.elice.ustory.global.redis.refresh;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

//@RedisHash(value = "jwtToken", timeToLive = 60 * 60 * 24 * 3)
//@AllArgsConstructor
//@Getter
public class RefreshToken {

//    @Id
//    private String id;
//    private String refreshToken;
//
//    @Indexed
//    private String accessToken;
}

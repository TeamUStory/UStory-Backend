package com.elice.ustory.global.redis.refresh;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value = "jwtToken")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RefreshToken {

    @Id
    private String id;
    private String refreshToken;

    @Indexed
    private String accessToken;

    @TimeToLive
    private Integer expiration;
}

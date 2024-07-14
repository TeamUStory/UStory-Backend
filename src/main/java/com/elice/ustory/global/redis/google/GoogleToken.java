package com.elice.ustory.global.redis.google;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value = "googleToken", timeToLive = 60 * 60 * 24 * 7)
@AllArgsConstructor
@NoArgsConstructor
public class GoogleToken {
    @Id
    private String id;

    @Getter
    private String googleToken;

    @Indexed
    private String accessToken;
}

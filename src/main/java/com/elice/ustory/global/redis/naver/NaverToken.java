package com.elice.ustory.global.redis.naver;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value = "naverToken", timeToLive = 60 * 60 * 24 * 7)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NaverToken {
    @Id
    private String id;
    private String naverToken;

    @Indexed
    private String accessToken;
}

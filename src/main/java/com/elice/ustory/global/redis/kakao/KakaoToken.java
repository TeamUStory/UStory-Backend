package com.elice.ustory.global.redis.kakao;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value = "kakaoToken", timeToLive = 60 * 60 * 24 * 7)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class KakaoToken {
    @Id
    private String id;
    private String kakaoToken;

    @Indexed
    private String accessToken;
}

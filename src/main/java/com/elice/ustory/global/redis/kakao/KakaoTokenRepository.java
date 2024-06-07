package com.elice.ustory.global.redis.kakao;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface KakaoTokenRepository extends CrudRepository<KakaoToken, String> {
    Optional<KakaoToken> findByAccessToken(String accessToken);
}

package com.elice.ustory.global.redis.naver;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface NaverTokenRepository extends CrudRepository<NaverToken, String> {
    Optional<NaverToken> findByAccessToken(String accessToken);
}

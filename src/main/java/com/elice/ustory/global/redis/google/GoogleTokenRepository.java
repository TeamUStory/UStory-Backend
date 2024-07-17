package com.elice.ustory.global.redis.google;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GoogleTokenRepository extends CrudRepository<GoogleToken, String> {
    Optional<GoogleToken> findByAccessToken(String accessToken);
}

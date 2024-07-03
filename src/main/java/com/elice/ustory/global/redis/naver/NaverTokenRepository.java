package com.elice.ustory.global.redis.naver;

import com.elice.ustory.global.redis.kakao.KakaoToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface NaverTokenRepository extends CrudRepository<NaverToken, String> {
    Optional<NaverToken> findByAccessToken(String accessToken);
}

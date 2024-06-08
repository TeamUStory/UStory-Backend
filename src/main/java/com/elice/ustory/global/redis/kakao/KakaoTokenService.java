package com.elice.ustory.global.redis.kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoTokenService {
    private final KakaoTokenRepository kakaoTokenRepository;

    public void saveKakaoTokenInfo(Long userId, String kakaoToken, String accessToken) {
        kakaoTokenRepository.save(new KakaoToken(String.valueOf(userId), kakaoToken, accessToken));
    }

    public void removeKakaoTokenInfo(String accessToken) {
        kakaoTokenRepository.findByAccessToken(accessToken)
                .ifPresent(kakaoTokenRepository::delete);
    }

    public Optional<KakaoToken> getByAccessToken(String accessToken){
        return kakaoTokenRepository.findByAccessToken(accessToken);
    }
}

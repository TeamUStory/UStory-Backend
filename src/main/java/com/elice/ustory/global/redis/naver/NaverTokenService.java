package com.elice.ustory.global.redis.naver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NaverTokenService {
    private final NaverTokenRepository naverTokenRepository;

    public void saveNaverTokenInfo(Long userId, String naverToken, String accessToken) {
        naverTokenRepository.save(new NaverToken(String.valueOf(userId), naverToken, accessToken));
    }

    public void removeNaverTokenInfo(String accessToken) {
        naverTokenRepository.findByAccessToken(accessToken)
                .ifPresent(naverTokenRepository::delete);
    }

    public Optional<NaverToken> getByAccessToken(String accessToken){
        return naverTokenRepository.findByAccessToken(accessToken);
    }
}

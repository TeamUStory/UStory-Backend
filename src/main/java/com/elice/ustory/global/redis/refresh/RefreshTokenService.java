package com.elice.ustory.global.redis.refresh;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void saveTokenInfo(Long userId, String refreshToken, String accessToken, int remainingTTL) {
        refreshTokenRepository.save(new RefreshToken(String.valueOf(userId), refreshToken, accessToken, remainingTTL));
    }

    public void removeTokenInfo(String accessToken) {
        refreshTokenRepository.findByAccessToken(accessToken)
                .ifPresent(refreshTokenRepository::delete);
    }

    public Optional<RefreshToken> getByAccessToken(String accessToken){
        return refreshTokenRepository.findByAccessToken(accessToken);
    }
}

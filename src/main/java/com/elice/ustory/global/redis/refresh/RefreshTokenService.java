package com.elice.ustory.global.redis.refresh;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveTokenInfo(Long userId, String refreshToken, String accessToken) {
        refreshTokenRepository.save(new RefreshToken(String.valueOf(userId), refreshToken, accessToken));
    }

    @Transactional
    public void removeTokenInfo(String accessToken) {
        refreshTokenRepository.findByAccessToken(accessToken)
                .ifPresent(refreshTokenRepository::delete);
    }
}

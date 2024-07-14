package com.elice.ustory.global.redis.google;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleTokenService {
    private final GoogleTokenRepository googleTokenRepository;

    public void saveGoogleTokenInfo(Long userId, String googleToken, String accessToken) {
        googleTokenRepository.save(new GoogleToken(String.valueOf(userId), googleToken, accessToken));
    }
}

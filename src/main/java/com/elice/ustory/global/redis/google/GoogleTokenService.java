package com.elice.ustory.global.redis.google;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleTokenService {
    private final GoogleTokenRepository googleTokenRepository;

    public void saveGoogleTokenInfo(Long userId, String googleToken, String accessToken) {
        googleTokenRepository.save(new GoogleToken(String.valueOf(userId), googleToken, accessToken));
    }

    public void removeGoogleTokenInfo(String accessToken) {
        googleTokenRepository.findByAccessToken(accessToken)
                .ifPresent(googleTokenRepository::delete);
    }

    public Optional<GoogleToken> getByAccessToken(String accessToken) {
        return googleTokenRepository.findByAccessToken(accessToken);
    }
}

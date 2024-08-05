package com.elice.ustory.global.redis.recommend;

import com.elice.ustory.domain.recommand.dto.RecommendRedisDTO;
import com.elice.ustory.global.exception.model.InternalServerException;
import com.elice.ustory.global.util.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendRedisService {

    private final RedisTemplate<String, RecommendRedisDTO> redisTemplate;
    private final RedisTemplate<String, Long> longRedisTemplate;

    private static final String KEY_PREFIX = "RecommendPaper";
    private static final String COUNTER_KEY = "RecommendPaperCounter";

    public void saveData(RecommendRedisDTO value) {
        Long counter = redisTemplate.opsForValue().increment(COUNTER_KEY);
        String key = KEY_PREFIX + counter;

        long secondsUntilMidnight = TimeUtils.getSecondsUntilMidnight();

        redisTemplate.opsForValue().set(key, value, secondsUntilMidnight, TimeUnit.SECONDS);

    }

    public RecommendRedisDTO getData(String key) {
        ValueOperations<String, RecommendRedisDTO> valueOps = redisTemplate.opsForValue();
        return valueOps.get(key);
    }

    public void deleteKeysWithPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys == null || keys.isEmpty()) {
            return;
        }

        redisTemplate.delete(keys);

        longRedisTemplate.opsForValue().set(COUNTER_KEY, 0L);
    }

}

package com.lemon.challenge.ratelimit.bucket4j;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.function.BiFunction;

@Service
public class TokenFactory {
    static final String SLIDING_WINDOW_ALGORITHM = "sliding_window";
    static final String FIXED_WINDOW_ALGORITHM = "fixed_window";

    static final Map<String, BiFunction<Long, Duration, Refill>> tokenGenerationStrategies = Map.of(
            // Tokens are generated ASAP. Example: if rate is 10 units per 10 seconds, one new unit will be created each second
            SLIDING_WINDOW_ALGORITHM, Refill::greedy,
            // Tokens are generated at the start of the interval. Example: if rate is 10 units per 10 seconds, every 10 seconds, 10 new unit are created.
            FIXED_WINDOW_ALGORITHM, Refill::intervally
    );

    private final BiFunction<Long, Duration, Refill> tokenGenerationStrategy;
    private final Long interval;
    private final Long quota;

    @Autowired
    public TokenFactory(@Value("${foaas.ratelimit.algorithm}") String algorithm,
                        @Value("${foaas.ratelimit.interval_in_seconds}") Long interval,
                        @Value("${foaas.ratelimit.quota}") Long quota) {
        this.tokenGenerationStrategy = tokenGenerationStrategies.get(algorithm);
        this.interval = interval;
        this.quota = quota;
    }

    public Bucket createTokens() {
        Refill tokenRefillStrategy = tokenGenerationStrategy.apply(quota, Duration.ofSeconds(interval));

        Bandwidth limit = Bandwidth.classic(quota, tokenRefillStrategy);

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}

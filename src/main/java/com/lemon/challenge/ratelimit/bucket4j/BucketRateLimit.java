package com.lemon.challenge.ratelimit.bucket4j;

import com.lemon.challenge.ratelimit.RateLimit;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Service
public class BucketRateLimit implements RateLimit {
    private final ConcurrentHashMap<String, Bucket> tokens;
    private final TokenFactory tokenFactory;

    @Autowired
    public BucketRateLimit(TokenFactory tokenFactory) {
        this.tokens = new ConcurrentHashMap<>();
        this.tokenFactory = tokenFactory;
    }

    @Override
    public <T> T withRateLimit(String clientKey, Supplier<T> task, Supplier<T> noQuotaHandler) {
        // Get client tokens or create news if they don't exist
        Bucket clientTokens = tokens.computeIfAbsent(clientKey, (key) -> tokenFactory.createTokens());

        // check if there is quota available
        if (clientTokens.tryConsume(1)) {
            return task.get();
        }

        return noQuotaHandler.get();
    }
}

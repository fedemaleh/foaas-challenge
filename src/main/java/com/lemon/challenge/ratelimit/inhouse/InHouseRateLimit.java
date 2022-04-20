package com.lemon.challenge.ratelimit.inhouse;

import com.lemon.challenge.ratelimit.RateLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Service
public class InHouseRateLimit implements RateLimit {
    private final ConcurrentHashMap<String, Quota> quotas;
    private final QuotaFactory quotaFactory;

    @Autowired
    public InHouseRateLimit(QuotaFactory quotaFactory) {
        this.quotas = new ConcurrentHashMap<>();
        this.quotaFactory = quotaFactory;
    }

    @Override
    public <T> T withRateLimit(String clientKey, Supplier<T> task, Supplier<T> noQuotaHandler) {
        Quota clientTokens = quotas.computeIfAbsent(clientKey, (key) -> quotaFactory.createQuota());

        if (clientTokens.tryConsume()) {
            return task.get();
        }

        return noQuotaHandler.get();
    }
}

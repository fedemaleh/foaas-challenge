package com.lemon.challenge.ratelimit;

import java.util.function.Supplier;

/**
 * Interface to abstract the rate limit algorithm. It allows the application to use different algorithm in a transparent fashion.
 * In this particular implementation, the algorithm will be set at startup by reading the `foaas.ratelimit.algoritm` configuration key.
 */
public interface RateLimit {
    /**
     * Applies the rate limit algorithm. If there is quota available then the `task` will be executed. Otherwise, the `noQuotaHandler` will be called.
     * @param clientKey client identifier to manage the quota.
     * @param task the task to perform when there is quota.
     * @param noQuotaHandler the recovery logic when there is no quota.
     * @param <T> The rate limit algorithm doesn't care about the result of the `task`. This generic parameter allows the rate limit to be abstracted of the task result.
     * @return The result of the executed callback.
     */
    <T> T withRateLimit(String clientKey, Supplier<T> task, Supplier<T> noQuotaHandler);
}

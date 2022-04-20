package com.lemon.challenge.ratelimit.inhouse;

import com.lemon.challenge.time.TimeService;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Rate Limit Quota using the Sliding Window algorithm. The quota is updated partially as soon as possible.
 * As an example, if rate is 10 units per 10 seconds, every 1 seconds, 1 new unit are created.
 */
public class SlidingWindowQuota implements Quota {
    private final TimeService timeService;
    private final Long interval;
    private final Long quota;

    private Instant lastGeneratedQuota;
    private AtomicLong availableQuota;

    public SlidingWindowQuota(TimeService timeService, Long interval, Long quota) {
        this.timeService = timeService;
        this.interval = interval;
        this.quota = quota;

        this.lastGeneratedQuota = timeService.now();
        this.availableQuota = new AtomicLong(quota);
    }

    @Override
    public boolean tryConsume() {
        // Always update the quota units before checking
        // This way we don't need to schedule the quota. By keeping the last time the quota was updated, we can know how many new units are needed.
        this.updateAvailableQuota();

        // We need to care about negative values, as the update is partial, it just adds new units proportional to the time elapsed.
        return availableQuota.updateAndGet((currentQuota) -> Math.max(-1, currentQuota - 1)) >= 0;
    }

    private void updateAvailableQuota() {
        Instant currentTimestamp = timeService.now();

        // The amount of units we need to add is proportional to the time passed.
        double units = Math.floor(quota * Duration.between(lastGeneratedQuota, currentTimestamp).getSeconds() / interval);

        if (units > 0) {
            this.lastGeneratedQuota = currentTimestamp;
            this.availableQuota.updateAndGet((currentQuota) -> (long) Math.min(quota, currentQuota + units));
        }
    }
}

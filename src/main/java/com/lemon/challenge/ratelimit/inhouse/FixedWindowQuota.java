package com.lemon.challenge.ratelimit.inhouse;

import com.lemon.challenge.time.TimeService;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Rate Limit Quota using the Fixed Window algorithm. The quota is restarted every `interval` seconds.
 * As an example, if rate is 10 units per 10 seconds, every 10 seconds, 10 new units are created.
 */
public class FixedWindowQuota implements Quota {
    private final TimeService timeService;
    private final Long interval;
    private final Long quota;

    private Instant lastGeneratedQuota;
    // Use AtomicLong to make operations atomic and concurrent-safe
    private final AtomicLong availableQuota;

    public FixedWindowQuota(TimeService timeService, Long interval, Long quota) {
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

        // It can get to negative values. This is not a problem as this strategy always sets the full quota and overrides whatever value the quota has.
        return availableQuota.decrementAndGet() >= 0;
    }

    private void updateAvailableQuota() {
        Instant currentTimestamp = timeService.now();

        // A new period started if a complete interval has passed since the last update
        // As an example, if the interval is 10 seconds and 15 has elapsed, then a new interval has started.
        // Given that this strategy will always put the full quota, we don't care how many periods elapsed.
        boolean newPeriodStarted = Duration.between(lastGeneratedQuota, currentTimestamp).compareTo(Duration.ofSeconds(interval)) > 0;

        if (newPeriodStarted) {
            this.lastGeneratedQuota = currentTimestamp;
            this.availableQuota.set(quota);
        }
    }
}

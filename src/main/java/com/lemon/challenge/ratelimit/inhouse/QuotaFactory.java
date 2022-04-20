package com.lemon.challenge.ratelimit.inhouse;

import com.lemon.challenge.time.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class QuotaFactory {
    static final String FIXED_WINDOW_ALGORITHM = "fixed_window";
    static final String SLIDING_WINDOW_ALGORITHM = "sliding_window";

    private final TimeService timeService;
    private final String algorithm;
    private final Long interval;
    private final Long quota;

    @Autowired
    public QuotaFactory(TimeService timeService,
                        @Value("${foaas.ratelimit.algorithm}") String algorithm,
                        @Value("${foaas.ratelimit.interval_in_seconds}") Long interval,
                        @Value("${foaas.ratelimit.quota}") Long quota) {
        this.timeService = timeService;
        this.algorithm = algorithm;
        this.interval = interval;
        this.quota = quota;
    }

    public Quota createQuota() {
        return switch (algorithm) {
            // Units are generated at the start of the interval. Example: if rate is 10 units per 10 seconds, every 10 seconds, 10 new unit are created.
            case FIXED_WINDOW_ALGORITHM -> new FixedWindowQuota(timeService, interval, quota);
            // Units are generated ASAP. Example: if rate is 10 units per 10 seconds, one new unit will be created each second
            default -> new SlidingWindowQuota(timeService, interval, quota);
        };
    }
}

package com.lemon.challenge.ratelimit.inhouse;

import com.lemon.challenge.time.TimeService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SlidingWindowRateLimitTest {
    private QuotaFactory quotaFactory;

    public InHouseRateLimit setupRateLimit(TimeService timeService, Long interval, Long quota) {
        quotaFactory = new QuotaFactory(timeService, QuotaFactory.SLIDING_WINDOW_ALGORITHM, interval, quota);

        return new InHouseRateLimit(quotaFactory);
    }

    @Test
    void when_limit_is_1_and_2_calls_are_made_same_client_the_second_should_reject() {
        /* Given:
         * A `TimeService` that simulates no time passed so the units are not restored,
         * An `interval` of 10 seconds,
         * A `quota` of 1
         */

        Instant fixedInstant = Instant.now(); // Simulate

        TimeService timeService = mock(TimeService.class);

        when(timeService.now()).thenReturn(fixedInstant);

        Long interval = 10L;
        Long quota = 1L;
        String client = "client";

        InHouseRateLimit rateLimit = setupRateLimit(timeService, interval, quota);

        /* When:
            * Two calls are made
           Then:
            * The first one is successful
            * The second one fails.
         */

        assertRateLimitSuccess(client, rateLimit);
        assertRateLimitFailure(client, rateLimit);
    }

    @Test
    void when_limit_is_2_and_2_calls_are_made_same_client_should_accept() {
        /* Given:
         * A `TimeService` that simulates no time passed so the units are not restored,
         * An `interval` of 10 seconds,
         * A `quota` of 2
         */

        Instant fixedInstant = Instant.now(); // Simulate

        TimeService timeService = mock(TimeService.class);

        when(timeService.now()).thenReturn(fixedInstant);

        Long interval = 10L;
        Long quota = 2L;
        String client = "client";

        InHouseRateLimit rateLimit = setupRateLimit(timeService, interval, quota);

        /* When:
            * Two calls are made
           Then:
            * Both are successful
         */

        assertRateLimitSuccess(client, rateLimit);
        assertRateLimitSuccess(client, rateLimit);
    }

    @Test
    void when_limit_is_1_and_2_calls_are_made_different_client_should_accept() {
        /* Given:
         * A `TimeService` that simulates no time passed so the units are not restored,
         * An `interval` of 10 seconds,
         * A `quota` of 1
         * 2 different client keys
         */

        Instant fixedInstant = Instant.now(); // Simulate

        TimeService timeService = mock(TimeService.class);

        when(timeService.now()).thenReturn(fixedInstant);

        Long interval = 10L;
        Long quota = 1L;
        String client1 = "client1";
        String client2 = "client2";

        InHouseRateLimit rateLimit = setupRateLimit(timeService, interval, quota);

        /* When:
            * Two calls are made
           Then:
            * Both are successful
         */

        assertRateLimitSuccess(client1, rateLimit);
        assertRateLimitSuccess(client2, rateLimit);
    }

    @Test
    void when_the_full_interval_elapses_quota_is_restored() {
        /* Given:
         * A `TimeService` that simulates 15 seconds elapses each time,
         * An `interval` of 10 seconds,
         * A `quota` of 1
         */

        Instant fixedInstant = Instant.now(); // Simulate

        List<Instant> instants = IntStream.range(1, 100)
                .mapToObj(i -> fixedInstant.plus(i * 15, ChronoUnit.SECONDS))
                .collect(Collectors.toList());


        TimeService timeService = mock(TimeService.class);

        when(timeService.now()).thenReturn(
                fixedInstant,
                instants.toArray(new Instant[]{})
        );

        Long interval = 10L;
        Long quota = 1L;
        String client = "client";

        InHouseRateLimit rateLimit = setupRateLimit(timeService, interval, quota);

        /* When:
            * Two calls are made
           Then:
            * Both are successful
         */

        assertRateLimitSuccess(client, rateLimit);
        assertRateLimitSuccess(client, rateLimit);
    }

    @Test
    void when_the_half_interval_elapses_quota_is_partially_restored() {
        /* Given:
         * A `TimeService` that simulates 5 seconds elapses each time,
         * An `interval` of 10 seconds,
         * A `quota` of 10
         */

        Instant fixedInstant = Instant.now(); // Simulate

        List<Instant> instants = IntStream.range(1, 100)
                .mapToObj(i -> fixedInstant.plus(i * 5, ChronoUnit.SECONDS))
                .collect(Collectors.toList());


        TimeService timeService = mock(TimeService.class);

        when(timeService.now()).thenReturn(
                fixedInstant,
                instants.toArray(new Instant[]{})
        );

        Long interval = 10L;
        Long quota = 1L;
        String client = "client";

        InHouseRateLimit rateLimit = setupRateLimit(timeService, interval, quota);

        /* When:
            * Two calls are made
           Then:
            * The first one is successful
            * The second one fails.
         */

        assertRateLimitSuccess(client, rateLimit);
        assertRateLimitSuccess(client, rateLimit);
    }

    private void assertRateLimitSuccess(String clientKey, InHouseRateLimit rateLimit) {
        rateLimit.withRateLimit(clientKey, () -> true, () -> {
            fail("Expected RateLimit to be successful but got rejected");

            return true;
        });
    }

    private void assertRateLimitFailure(String clientKey, InHouseRateLimit rateLimit) {
        rateLimit.withRateLimit(clientKey, () -> {
                    fail("Expected RateLimit to be rejected but was successful");

                    return true;
                },
                () -> true
        );
    }
}
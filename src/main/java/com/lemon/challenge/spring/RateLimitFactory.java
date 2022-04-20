package com.lemon.challenge.spring;

import com.lemon.challenge.ratelimit.bucket4j.BucketRateLimit;
import com.lemon.challenge.ratelimit.RateLimit;
import com.lemon.challenge.ratelimit.inhouse.InHouseRateLimit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RateLimitFactory {
    static final String IN_HOUSE_IMPLEMENTATION = "in_house";
    static final String BUCKET4j_IMPLEMENTATION = "bucket4j";

    @Bean
    @Primary
    public RateLimit rateLimit(@Value("${foaas.ratelimit.implementation}") String implementation,
                               ConfigurableApplicationContext context) {
        // A different implementation can be used depending on the configuration.
        if (IN_HOUSE_IMPLEMENTATION.equals(implementation)) {
            return context.getBean(InHouseRateLimit.class);
        }

        return context.getBean(BucketRateLimit.class);

    }
}

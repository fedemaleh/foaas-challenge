package com.lemon.challenge.time;

import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * This class is an utility to make unit testing possible as it allows to mock the pass of time.
 */
@Service
public class TimeService {

    public Instant now() {
        return Instant.now();
    }
}

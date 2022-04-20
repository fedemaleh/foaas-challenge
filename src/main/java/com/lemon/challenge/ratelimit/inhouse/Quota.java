package com.lemon.challenge.ratelimit.inhouse;

public interface Quota {
    /**
     * Tries to consume an unit from the available quota. If there is quota available it will consume one unit and return true.
     * Otherwise it will not consume and return false.
     * @return whether there is quota available;
     */
    public boolean tryConsume();
}

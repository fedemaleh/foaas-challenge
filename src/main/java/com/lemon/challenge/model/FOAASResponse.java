package com.lemon.challenge.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FOAASResponse {
    private final String message;
    private final String subtitle;

    @JsonCreator
    public FOAASResponse(@JsonProperty("message") String message, @JsonProperty("subtitle") String subtitle) {
        this.message = message;
        this.subtitle = subtitle;
    }

    public String getMessage() {
        return message;
    }

    public String getSubtitle() {
        return subtitle;
    }
}

package com.lemon.challenge.model;

public class MessagesResponse {
    private final String message;
    private final String subtitle;

    public MessagesResponse(String message, String subtitle) {
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

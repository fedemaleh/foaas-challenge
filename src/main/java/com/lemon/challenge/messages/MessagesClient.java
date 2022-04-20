package com.lemon.challenge.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon.challenge.model.FOAASResponse;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
public class MessagesClient {
    private final ObjectMapper objectMapper;
    private final AsyncHttpClient client;

    private static final String BASE_DOMAIN = "https://foaas.com";
    private static final List<String> MESSAGES = Arrays.asList(
            "%s/asshole/%s",
            "%s/awesome/%s",
            "%s/bag/%s",
            "%s/bucket/%s",
            "%s/cool/%s",
            "%s/cup/%s",
            "%s/dense/%s",
            "%s/diabetes/%s",
            "%s/even/%s",
            "%s/everyone/%s",
            "%s/everything/%s",
            "%s/family/%s"
    );

    @Autowired
    public MessagesClient(ObjectMapper objectMapper, AsyncHttpClient client) {
        this.objectMapper = objectMapper;
        this.client = client;
    }

    public CompletableFuture<FOAASResponse> getMessage() {
        String message = MESSAGES.get(new Random().nextInt(MESSAGES.size()));

        return client.prepareGet(String.format(message, BASE_DOMAIN, "Fede"))
                .addHeader("Accept", "application/json")
                .execute()
                .toCompletableFuture()
                .thenApply(Response::getResponseBody)
                .thenApply(body -> {
                    try {
                        return objectMapper.readValue(body, FOAASResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException();
                    }
                });
    }
}

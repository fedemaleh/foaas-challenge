package com.lemon.challenge.messages;

import com.lemon.challenge.model.MessagesResponse;
import com.lemon.challenge.ratelimit.RateLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class MessagesController {
    private final MessagesClient client;
    private final RateLimit rateLimit;

    @Autowired
    public MessagesController(MessagesClient client, RateLimit rateLimit) {
        this.client = client;
        this.rateLimit = rateLimit;
    }

    @RequestMapping(path = "/message", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<MessagesResponse>> message(@RequestParam("client_id") String clientId) {
        return rateLimit.withRateLimit(
                clientId,
                () -> client.getMessage()
                        .thenApply(foaas -> new MessagesResponse(foaas.getMessage(), foaas.getSubtitle()))
                        .thenApply(ResponseEntity::ok),
                () -> CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(new MessagesResponse("Exceeded Quota", "Retry after 10 seconds"))
                )
        );
    }
}

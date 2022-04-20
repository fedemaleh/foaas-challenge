package com.lemon.challenge.messages;

import com.lemon.challenge.model.MessagesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class MessagesController {

    @Autowired
    public MessagesController(){}

    @RequestMapping(path = "/message", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<MessagesResponse>> message() {
        return CompletableFuture.completedFuture(ResponseEntity.ok(new MessagesResponse("hola", "fede")));
    }
}

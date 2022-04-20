package com.lemon.challenge.spring;

import org.asynchttpclient.AsyncHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.asynchttpclient.Dsl.asyncHttpClient;

@Configuration
public class HttpClientFactory {
    @Bean
    public AsyncHttpClient client(){
        return asyncHttpClient();
    }
}

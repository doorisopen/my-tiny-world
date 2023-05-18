package com.twlee.bank.common.infra;

import com.twlee.bank.common.application.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SlackNotification implements NotificationService {

    @Value("${slack.hook.url}")
    private String hookUrl;

    @Override
    public void send(String message) {
        WebClient webClient = WebClient.create();

        // Request payload
        String requestBody = "{\"text\": \"" + message + "\"}";

        // POST request
        Mono<String> responseMono = webClient.post()
                .uri(hookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(String.class);

        // Handling the response
        responseMono.subscribe(response -> log.info("response: {}", response));
    }
}

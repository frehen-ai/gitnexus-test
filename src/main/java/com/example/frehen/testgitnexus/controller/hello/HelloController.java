package com.example.frehen.testgitnexus.controller.hello;

import com.example.frehen.testgitnexus.repository.MessageRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.Instant;

@RestController
public class HelloController {

    public record HelloRequest(String user, String message) {}

    public record HelloResponse(String message) {}

    private final MessageRepository messageRepository;
    private final Clock clock;

    public HelloController(MessageRepository messageRepository, Clock clock) {
        this.messageRepository = messageRepository;
        this.clock = clock;
    }

    @PostMapping("/hello")
    public HelloResponse sayHello(@RequestBody HelloRequest request) {
        messageRepository.save(request.user(), request.message(), Instant.now(clock));
        return new HelloResponse("Hello " + request.user() + ", " + request.message());
    }
}

package com.example.frehen.testgitnexus.service;

import com.example.frehen.testgitnexus.controller.hello.HelloController.HelloResponse;
import com.example.frehen.testgitnexus.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Service
public class HelloService {

    private final MessageRepository messageRepository;
    private final Clock clock;
    private final TimeRounder timeRounder;

    public HelloService(MessageRepository messageRepository, Clock clock, TimeRounder timeRounder) {
        this.messageRepository = messageRepository;
        this.clock = clock;
        this.timeRounder = timeRounder;
    }

    public HelloResponse sayHello(String user, String message) {
        Instant now = Instant.now(clock);
        Instant rounded = timeRounder.round(now);
        messageRepository.save(user, message, rounded);
        return new HelloResponse("Hello " + user + ", " + message);
    }
}

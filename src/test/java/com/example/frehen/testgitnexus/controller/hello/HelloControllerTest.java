package com.example.frehen.testgitnexus.controller.hello;

import com.example.frehen.testgitnexus.repository.InMemoryMessageRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloControllerTest {

    private final Clock clock = Clock.fixed(Instant.parse("2024-01-01T12:00:00Z"), ZoneOffset.UTC);
    private final HelloController controller = new HelloController(new InMemoryMessageRepository(), clock);

    @Test
    void sayHello_returnsFormattedMessage() {
        var request = new HelloController.HelloRequest("Alice", "welcome aboard");
        var response = controller.sayHello(request);
        assertEquals("Hello Alice, welcome aboard", response.message());
    }
}

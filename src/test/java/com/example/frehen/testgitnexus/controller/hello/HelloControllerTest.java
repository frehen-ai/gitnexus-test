package com.example.frehen.testgitnexus.controller.hello;

import com.example.frehen.testgitnexus.repository.InMemoryMessageRepository;
import com.example.frehen.testgitnexus.service.HelloService;
import com.example.frehen.testgitnexus.service.TimeRounder;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloControllerTest {

    private final Clock clock = Clock.fixed(Instant.parse("2024-01-01T12:00:00Z"), ZoneOffset.UTC);
    private final TimeRounder timeRounder = new TimeRounder(Duration.ofSeconds(1));
    private final HelloService helloService = new HelloService(new InMemoryMessageRepository(), clock, timeRounder);
    private final HelloController controller = new HelloController(helloService);

    @Test
    void sayHello_returnsFormattedMessage() {
        var request = new HelloController.HelloRequest("Alice", "welcome aboard");
        var response = controller.sayHello(request);
        assertEquals("Hello Alice, welcome aboard", response.message());
    }
}

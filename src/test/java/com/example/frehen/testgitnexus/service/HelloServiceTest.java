package com.example.frehen.testgitnexus.service;

import com.example.frehen.testgitnexus.controller.hello.HelloController.HelloResponse;
import com.example.frehen.testgitnexus.repository.InMemoryMessageRepository;
import com.example.frehen.testgitnexus.repository.Message;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HelloServiceTest {

    @Test
    void sayHello_roundsTimestampDown() {
        var repo = new InMemoryMessageRepository();
        var clock = Clock.fixed(Instant.parse("2024-01-01T10:00:17Z"), ZoneOffset.UTC);
        var rounder = new TimeRounder(Duration.ofSeconds(10));
        var service = new HelloService(repo, clock, rounder);

        service.sayHello("Alice", "hello");

        List<Message> messages = repo.findByInterval(
                Instant.parse("2024-01-01T10:00:10Z"),
                Instant.parse("2024-01-01T10:00:10Z"));

        assertEquals(1, messages.size());
        assertEquals("Alice", messages.getFirst().user());
        assertEquals("hello", messages.getFirst().message());
        assertEquals(Instant.parse("2024-01-01T10:00:10Z"), messages.getFirst().timestamp());
    }

    @Test
    void sayHello_returnsFormattedResponse() {
        var repo = new InMemoryMessageRepository();
        var clock = Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneOffset.UTC);
        var rounder = new TimeRounder(Duration.ofSeconds(1));
        var service = new HelloService(repo, clock, rounder);

        HelloResponse response = service.sayHello("Alice", "welcome aboard");

        assertEquals("Hello Alice, welcome aboard", response.message());
    }

    @Test
    void messagesWithinSameBucket_getSameTimestamp() {
        var repo = new InMemoryMessageRepository();
        var rounder = new TimeRounder(Duration.ofSeconds(10));

        var clock1 = Clock.fixed(Instant.parse("2024-01-01T10:00:12Z"), ZoneOffset.UTC);
        new HelloService(repo, clock1, rounder).sayHello("Alice", "first");

        var clock2 = Clock.fixed(Instant.parse("2024-01-01T10:00:18Z"), ZoneOffset.UTC);
        new HelloService(repo, clock2, rounder).sayHello("Bob", "second");

        List<Message> messages = repo.findByInterval(
                Instant.parse("2024-01-01T10:00:10Z"),
                Instant.parse("2024-01-01T10:00:10Z"));

        assertEquals(2, messages.size());
        assertEquals(messages.get(0).timestamp(), messages.get(1).timestamp());
    }

    @Test
    void messagesInDifferentBuckets_getDifferentTimestamps() {
        var repo = new InMemoryMessageRepository();
        var rounder = new TimeRounder(Duration.ofSeconds(10));

        var clock1 = Clock.fixed(Instant.parse("2024-01-01T10:00:18Z"), ZoneOffset.UTC);
        new HelloService(repo, clock1, rounder).sayHello("Alice", "first");

        var clock2 = Clock.fixed(Instant.parse("2024-01-01T10:00:22Z"), ZoneOffset.UTC);
        new HelloService(repo, clock2, rounder).sayHello("Bob", "second");

        List<Message> bucket1 = repo.findByInterval(
                Instant.parse("2024-01-01T10:00:10Z"),
                Instant.parse("2024-01-01T10:00:10Z"));
        assertEquals(1, bucket1.size());
        assertEquals("first", bucket1.getFirst().message());

        List<Message> bucket2 = repo.findByInterval(
                Instant.parse("2024-01-01T10:00:20Z"),
                Instant.parse("2024-01-01T10:00:20Z"));
        assertEquals(1, bucket2.size());
        assertEquals("second", bucket2.getFirst().message());
    }

    @Test
    void sayHello_with1HourInterval_roundsCorrectly() {
        var repo = new InMemoryMessageRepository();
        var clock = Clock.fixed(Instant.parse("2024-01-01T10:33:17Z"), ZoneOffset.UTC);
        var rounder = new TimeRounder(Duration.ofHours(1));
        var service = new HelloService(repo, clock, rounder);

        service.sayHello("Alice", "hello");

        List<Message> messages = repo.findByInterval(
                Instant.parse("2024-01-01T10:00:00Z"),
                Instant.parse("2024-01-01T10:00:00Z"));

        assertEquals(1, messages.size());
        assertEquals(Instant.parse("2024-01-01T10:00:00Z"), messages.getFirst().timestamp());
    }
}

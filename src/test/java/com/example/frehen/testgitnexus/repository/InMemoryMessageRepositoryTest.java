package com.example.frehen.testgitnexus.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryMessageRepositoryTest {

    private InMemoryMessageRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryMessageRepository();
    }

    @Test
    void save_and_findByInterval_returnsMessagesInOrder() {
        Instant t1 = Instant.parse("2024-01-01T10:00:00Z");
        Instant t2 = Instant.parse("2024-01-01T10:01:00Z");
        Instant t3 = Instant.parse("2024-01-01T10:02:00Z");

        repository.save("Alice", "first", t1);
        repository.save("Bob", "second", t2);
        repository.save("Alice", "third", t3);

        List<Message> result = repository.findByInterval(t1, t3);

        assertEquals(3, result.size());
        assertEquals("first", result.get(0).message());
        assertEquals("second", result.get(1).message());
        assertEquals("third", result.get(2).message());
    }

    @Test
    void findByInterval_excludesOutsideRange() {
        Instant t1 = Instant.parse("2024-01-01T09:00:00Z");
        Instant t2 = Instant.parse("2024-01-01T10:00:00Z");
        Instant t3 = Instant.parse("2024-01-01T11:00:00Z");

        repository.save("Alice", "before", t1);
        repository.save("Bob", "inside", t2);
        repository.save("Alice", "after", t3);

        List<Message> result = repository.findByInterval(
                Instant.parse("2024-01-01T09:30:00Z"),
                Instant.parse("2024-01-01T10:30:00Z"));

        assertEquals(1, result.size());
        assertEquals("inside", result.get(0).message());
    }

    @Test
    void findByIntervalAndUsers_filtersByUser() {
        Instant t1 = Instant.parse("2024-01-01T10:00:00Z");
        Instant t2 = Instant.parse("2024-01-01T10:01:00Z");
        Instant t3 = Instant.parse("2024-01-01T10:02:00Z");

        repository.save("Alice", "msg1", t1);
        repository.save("Bob", "msg2", t2);
        repository.save("Charlie", "msg3", t3);

        List<Message> result = repository.findByIntervalAndUsers(t1, t3, List.of("Alice", "Charlie"));

        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).user());
        assertEquals("Charlie", result.get(1).user());
    }

    @Test
    void findByIntervalAndUsers_singleUser() {
        Instant t1 = Instant.parse("2024-01-01T10:00:00Z");
        Instant t2 = Instant.parse("2024-01-01T10:01:00Z");

        repository.save("Alice", "msg1", t1);
        repository.save("Bob", "msg2", t2);

        List<Message> result = repository.findByIntervalAndUsers(t1, t2, List.of("Bob"));

        assertEquals(1, result.size());
        assertEquals("Bob", result.get(0).user());
    }

    @Test
    void multipleMessages_sameTimestamp_preserveArrivalOrder() {
        Instant t = Instant.parse("2024-01-01T10:00:00Z");

        repository.save("Alice", "first", t);
        repository.save("Bob", "second", t);
        repository.save("Charlie", "third", t);

        List<Message> result = repository.findByInterval(t, t);

        assertEquals(3, result.size());
        assertEquals("first", result.get(0).message());
        assertEquals("second", result.get(1).message());
        assertEquals("third", result.get(2).message());
    }

    @Test
    void findByInterval_emptyRepository_returnsEmpty() {
        List<Message> result = repository.findByInterval(Instant.MIN, Instant.MAX);
        assertTrue(result.isEmpty());
    }
}

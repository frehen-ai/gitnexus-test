package com.example.frehen.testgitnexus.repository;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Repository
public class InMemoryMessageRepository implements MessageRepository {

    private final ConcurrentNavigableMap<Instant, List<Message>> store = new ConcurrentSkipListMap<>();

    @Override
    public void save(String user, String message, Instant timestamp) {
        store.computeIfAbsent(timestamp, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(new Message(user, message, timestamp));
    }

    @Override
    public List<Message> findByInterval(Instant from, Instant to) {
        return store.subMap(from, true, to, true)
                .values().stream()
                .flatMap(List::stream)
                .toList();
    }

    @Override
    public List<Message> findByIntervalAndUsers(Instant from, Instant to, List<String> users) {
        return store.subMap(from, true, to, true)
                .values().stream()
                .flatMap(List::stream)
                .filter(m -> users.contains(m.user()))
                .toList();
    }
}

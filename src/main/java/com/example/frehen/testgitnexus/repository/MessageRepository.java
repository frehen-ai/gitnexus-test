package com.example.frehen.testgitnexus.repository;

import java.time.Instant;
import java.util.List;

public interface MessageRepository {

    void save(String user, String message, Instant timestamp);

    List<Message> findByInterval(Instant from, Instant to);

    List<Message> findByIntervalAndUsers(Instant from, Instant to, List<String> users);
}

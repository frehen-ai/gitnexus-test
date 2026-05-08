package com.example.frehen.testgitnexus.repository;

import java.time.Instant;

public record Message(String user, String message, Instant timestamp) {}

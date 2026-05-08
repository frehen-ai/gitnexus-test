package com.example.frehen.testgitnexus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TimeRounderTest {

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 10, 12, 15, 20, 30})
    void validSecondIntervals(long seconds) {
        assertDoesNotThrow(() -> new TimeRounder(Duration.ofSeconds(seconds)));
    }

    @ParameterizedTest
    @ValueSource(longs = {7, 8, 9, 11, 13, 14, 16, 17, 18, 19, 21, 25})
    void invalidSecondIntervals(long seconds) {
        assertThrows(IllegalArgumentException.class, () -> new TimeRounder(Duration.ofSeconds(seconds)));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 10, 12, 15, 20, 30})
    void validMinuteIntervals(long minutes) {
        assertDoesNotThrow(() -> new TimeRounder(Duration.ofMinutes(minutes)));
    }

    @ParameterizedTest
    @ValueSource(longs = {7, 8, 9, 11, 13, 14, 16, 17, 18, 19, 21, 25})
    void invalidMinuteIntervals(long minutes) {
        assertThrows(IllegalArgumentException.class, () -> new TimeRounder(Duration.ofMinutes(minutes)));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 6, 8, 12})
    void validHourIntervals(long hours) {
        assertDoesNotThrow(() -> new TimeRounder(Duration.ofHours(hours)));
    }

    @ParameterizedTest
    @ValueSource(longs = {5, 7, 9, 10, 11, 13})
    void invalidHourIntervals(long hours) {
        assertThrows(IllegalArgumentException.class, () -> new TimeRounder(Duration.ofHours(hours)));
    }

    @Test
    void rejectsIntervalOf24HoursOrMore() {
        assertThrows(IllegalArgumentException.class, () -> new TimeRounder(Duration.ofHours(24)));
        assertThrows(IllegalArgumentException.class, () -> new TimeRounder(Duration.ofHours(48)));
    }

    @Test
    void rejectsZeroAndNegative() {
        assertThrows(IllegalArgumentException.class, () -> new TimeRounder(Duration.ZERO));
        assertThrows(IllegalArgumentException.class, () -> new TimeRounder(Duration.ofSeconds(-1)));
    }

    @Test
    void roundsDown_10SecondInterval() {
        var rounder = new TimeRounder(Duration.ofSeconds(10));

        assertEquals(
                Instant.parse("2024-01-01T10:00:10Z"),
                rounder.round(Instant.parse("2024-01-01T10:00:17Z")));
        assertEquals(
                Instant.parse("2024-01-01T10:00:50Z"),
                rounder.round(Instant.parse("2024-01-01T10:00:59Z")));
        assertEquals(
                Instant.parse("2024-01-01T10:00:00Z"),
                rounder.round(Instant.parse("2024-01-01T10:00:03Z")));
    }

    @Test
    void roundsDown_exactBoundaryUnchanged() {
        var rounder = new TimeRounder(Duration.ofSeconds(10));

        assertEquals(
                Instant.parse("2024-01-01T10:00:10Z"),
                rounder.round(Instant.parse("2024-01-01T10:00:10Z")));
    }

    @Test
    void roundsDown_1SecondInterval() {
        var rounder = new TimeRounder(Duration.ofSeconds(1));

        // Sub-second nanos are stripped
        Instant withNanos = Instant.parse("2024-01-01T10:00:17Z").plusNanos(500_000_000);
        assertEquals(
                Instant.parse("2024-01-01T10:00:17Z"),
                rounder.round(withNanos));
    }

    @Test
    void roundsDown_15MinuteInterval() {
        var rounder = new TimeRounder(Duration.ofMinutes(15));

        assertEquals(
                Instant.parse("2024-01-01T10:00:00Z"),
                rounder.round(Instant.parse("2024-01-01T10:07:33Z")));
        assertEquals(
                Instant.parse("2024-01-01T10:15:00Z"),
                rounder.round(Instant.parse("2024-01-01T10:22:00Z")));
        assertEquals(
                Instant.parse("2024-01-01T10:45:00Z"),
                rounder.round(Instant.parse("2024-01-01T10:59:59Z")));
    }

    @Test
    void roundsDown_1HourInterval() {
        var rounder = new TimeRounder(Duration.ofHours(1));

        assertEquals(
                Instant.parse("2024-01-01T10:00:00Z"),
                rounder.round(Instant.parse("2024-01-01T10:33:17Z")));
        assertEquals(
                Instant.parse("2024-01-01T23:00:00Z"),
                rounder.round(Instant.parse("2024-01-01T23:59:59Z")));
    }

    @Test
    void roundsDown_crossMinuteBoundary() {
        var rounder = new TimeRounder(Duration.ofSeconds(10));

        // 10:01:07 -> 10:01:00
        assertEquals(
                Instant.parse("2024-01-01T10:01:00Z"),
                rounder.round(Instant.parse("2024-01-01T10:01:07Z")));
    }
}

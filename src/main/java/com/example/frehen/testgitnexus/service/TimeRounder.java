package com.example.frehen.testgitnexus.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class TimeRounder {

    private final long intervalSeconds;

    public TimeRounder(@Value("${message.aggregate-interval:PT1S}") Duration interval) {
        this.intervalSeconds = interval.toSeconds();
        if (intervalSeconds <= 0) {
            throw new IllegalArgumentException("Interval must be positive, got: " + interval);
        }
        validate(interval);
    }

    private void validate(Duration interval) {
        long seconds = interval.toSeconds();

        if (seconds < 60) {
            // Must evenly divide 60 seconds
            if (60 % seconds != 0) {
                throw new IllegalArgumentException(
                        "Interval of %ds is not a whole divisor of 60 seconds".formatted(seconds));
            }
        } else if (seconds < 3600) {
            // Must be whole minutes that evenly divide 60 minutes
            if (seconds % 60 != 0) {
                throw new IllegalArgumentException(
                        "Interval must be a whole number of minutes, got %ds".formatted(seconds));
            }
            long minutes = seconds / 60;
            if (60 % minutes != 0) {
                throw new IllegalArgumentException(
                        "Interval of %d minutes is not a whole divisor of 60 minutes".formatted(minutes));
            }
        } else if (seconds < 86400) {
            // Must be whole hours that evenly divide 24 hours
            if (seconds % 3600 != 0) {
                throw new IllegalArgumentException(
                        "Interval must be a whole number of hours, got %ds".formatted(seconds));
            }
            long hours = seconds / 3600;
            if (24 % hours != 0) {
                throw new IllegalArgumentException(
                        "Interval of %d hours is not a whole divisor of 24 hours".formatted(hours));
            }
        } else {
            throw new IllegalArgumentException(
                    "Interval must be less than 24 hours, got %ds".formatted(seconds));
        }
    }

    public Instant round(Instant timestamp) {
        long epochSecond = timestamp.getEpochSecond();
        long rounded = epochSecond - (epochSecond % intervalSeconds);
        return Instant.ofEpochSecond(rounded);
    }
}

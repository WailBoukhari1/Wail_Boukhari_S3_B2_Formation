package com.formation.exception;

import java.time.LocalDateTime;

public abstract class BaseException extends RuntimeException {
    private final LocalDateTime timestamp;

    protected BaseException(String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
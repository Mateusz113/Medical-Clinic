package com.example.demo.exception;

import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public abstract class WebException extends RuntimeException {
    private final HttpStatus statusCode;
    private final OffsetDateTime date;

    public WebException(String message, HttpStatus statusCode, OffsetDateTime date) {
        super(message);
        this.statusCode = statusCode;
        this.date = date;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public OffsetDateTime getDate() {
        return date;
    }
}

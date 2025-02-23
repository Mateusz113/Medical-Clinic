package com.example.demo.model;

import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public record ErrorMessage(
        String message,
        HttpStatus statusCode,
        OffsetDateTime date
) {}

package com.example.demo.exception;

import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public class PatientOperationException extends WebException {
    public PatientOperationException(String message, OffsetDateTime date) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, date);
    }
}

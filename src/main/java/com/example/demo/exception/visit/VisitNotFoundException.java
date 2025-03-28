package com.example.demo.exception.visit;

import com.example.demo.exception.WebException;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public class VisitNotFoundException extends WebException {
    public VisitNotFoundException(String message, OffsetDateTime date) {
        super(message, HttpStatus.NOT_FOUND, date);
    }
}

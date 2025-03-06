package com.example.demo.exception.patient;

import com.example.demo.exception.WebException;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public class PatientNotFoundException extends WebException {
    public PatientNotFoundException(String message, OffsetDateTime date) {
        super(message, HttpStatus.NOT_FOUND, date);
    }
}

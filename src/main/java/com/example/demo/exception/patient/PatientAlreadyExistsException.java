package com.example.demo.exception.patient;

import com.example.demo.exception.WebException;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public class PatientAlreadyExistsException extends WebException {
    public PatientAlreadyExistsException(String message, OffsetDateTime date) {
        super(message, HttpStatus.CONFLICT, date);
    }
}

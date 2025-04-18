package com.example.demo.exception.patient;

import com.example.demo.exception.WebException;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public class PatientIllegalDataException extends WebException {
    public PatientIllegalDataException(String message, OffsetDateTime date) {
        super(message, HttpStatus.BAD_REQUEST, date);
    }
}

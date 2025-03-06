package com.example.demo.exception.doctor;

import com.example.demo.exception.WebException;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public class DoctorNotFoundException extends WebException {
    public DoctorNotFoundException(String message, OffsetDateTime date) {
        super(message, HttpStatus.NOT_FOUND, date);
    }
}

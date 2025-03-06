package com.example.demo.exception.doctor;

import com.example.demo.exception.WebException;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public class DoctorAlreadyExistsException extends WebException {
    public DoctorAlreadyExistsException(String message, OffsetDateTime date) {
        super(message, HttpStatus.CONFLICT, date);
    }
}

package com.example.demo.exception.doctor;

import com.example.demo.exception.WebException;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public class DoctorIllegalDataException extends WebException {
    public DoctorIllegalDataException(String message, OffsetDateTime date) {
        super(message, HttpStatus.BAD_REQUEST, date);
    }
}

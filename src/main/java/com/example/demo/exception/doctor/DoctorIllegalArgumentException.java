package com.example.demo.exception.doctor;

import com.example.demo.exception.WebException;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public class DoctorIllegalArgumentException extends WebException {
    public DoctorIllegalArgumentException(String message, OffsetDateTime date) {
        super(message, HttpStatus.BAD_REQUEST, date);
    }
}

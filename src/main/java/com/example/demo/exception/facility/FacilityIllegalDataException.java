package com.example.demo.exception.facility;

import com.example.demo.exception.WebException;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public class FacilityIllegalDataException extends WebException {
    public FacilityIllegalDataException(String message, OffsetDateTime date) {
        super(message, HttpStatus.BAD_REQUEST, date);
    }
}

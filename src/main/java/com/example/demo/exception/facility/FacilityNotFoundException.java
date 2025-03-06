package com.example.demo.exception.facility;

import com.example.demo.exception.WebException;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public class FacilityNotFoundException extends WebException {
    public FacilityNotFoundException(String message, OffsetDateTime date) {
        super(message, HttpStatus.NOT_FOUND, date);
    }
}

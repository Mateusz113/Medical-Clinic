package com.example.demo.exception.facility;

import com.example.demo.exception.WebException;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public class FacilityAlreadyExistsException extends WebException {
    public FacilityAlreadyExistsException(String message, OffsetDateTime date) {
        super(message, HttpStatus.CONFLICT, date);
    }
}

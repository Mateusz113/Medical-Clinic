package com.example.demo.handler;

import com.example.demo.exception.PatientAlreadyExistsException;
import com.example.demo.exception.PatientNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class MedicalClinicExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(exception = PatientNotFoundException.class)
    public ResponseEntity<Object> handlePatientNotFoundException(RuntimeException ex, WebRequest request) {
        String body = ex.getMessage();
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(exception = PatientAlreadyExistsException.class)
    public ResponseEntity<Object> handlePatientAlreadyExistsException(RuntimeException ex, WebRequest request) {
        String body = ex.getMessage();
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }
}

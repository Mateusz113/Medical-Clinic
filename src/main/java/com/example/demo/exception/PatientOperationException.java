package com.example.demo.exception;

public class PatientOperationException extends RuntimeException {
    public PatientOperationException(String message) {
        super(message);
    }
}

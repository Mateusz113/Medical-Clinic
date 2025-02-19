package com.example.demo.exception;

public class PatientDeletionException extends RuntimeException {
    public PatientDeletionException(String message) {
        super(message);
    }
}

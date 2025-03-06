package com.example.demo.model.doctor;

public record DoctorDataDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        String specialization
) {
}

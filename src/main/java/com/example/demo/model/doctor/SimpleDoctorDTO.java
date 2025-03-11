package com.example.demo.model.doctor;

public record SimpleDoctorDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        String specialization
) {
}

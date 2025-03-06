package com.example.demo.model.doctor;

public record FullDoctorDataDTO(
        String email,
        String password,
        String firstName,
        String lastName,
        String specialization
) {
}

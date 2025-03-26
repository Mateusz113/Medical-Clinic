package com.example.demo.model.doctor;

import lombok.Builder;

@Builder
public record SimpleDoctorDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        String specialization
) {
}

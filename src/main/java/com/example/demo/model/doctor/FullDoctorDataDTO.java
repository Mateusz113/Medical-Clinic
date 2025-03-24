package com.example.demo.model.doctor;

import lombok.Builder;

@Builder
public record FullDoctorDataDTO(
        String email,
        String password,
        String firstName,
        String lastName,
        String specialization
) {
}

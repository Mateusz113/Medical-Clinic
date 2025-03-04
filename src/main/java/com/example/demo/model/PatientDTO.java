package com.example.demo.model;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PatientDTO(
        Long id,
        String email,
        String idCardNo,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthday
) {
}

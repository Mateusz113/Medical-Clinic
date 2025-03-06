package com.example.demo.model.patient;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record FullPatientDataDTO(
        String email,
        String password,
        String idCardNo,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthday
) {
}

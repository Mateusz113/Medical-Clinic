package com.example.demo.command.patient;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpsertPatientCommand(
        String email,
        String password,
        String idCardNo,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthday
) {
}

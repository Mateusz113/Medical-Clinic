package com.example.demo.command.doctor;

import lombok.Builder;

@Builder
public record UpsertDoctorCommand(
        String email,
        String password,
        String firstName,
        String lastName,
        String specialization
) {
}

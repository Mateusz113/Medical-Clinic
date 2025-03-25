package com.example.demo.command.doctor;

import lombok.Builder;

import java.util.List;

@Builder
public record UpdateDoctorFacilitiesCommand(
        List<Long> facilitiesIds
) {
}

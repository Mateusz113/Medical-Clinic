package com.example.demo.command.facility;

import com.example.demo.command.doctor.UpsertDoctorCommand;
import lombok.Builder;

import java.util.List;

@Builder
public record UpsertFacilityCommand(
        String name,
        String city,
        String zipCode,
        String street,
        String buildingNumber,
        List<UpsertDoctorCommand> doctors
) {
}

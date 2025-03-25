package com.example.demo.model.facility;

import com.example.demo.command.doctor.UpsertDoctorCommand;

import java.util.List;

public record FullFacilityDataDTO(
        String name,
        String city,
        String zipCode,
        String street,
        String buildingNumber,
        List<UpsertDoctorCommand> doctors
) {
}

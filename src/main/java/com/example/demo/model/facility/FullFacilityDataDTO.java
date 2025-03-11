package com.example.demo.model.facility;

import com.example.demo.model.doctor.FullDoctorDataDTO;

import java.util.Set;

public record FullFacilityDataDTO(
        String name,
        String city,
        String zipCode,
        String street,
        String buildingNumber,
        Set<FullDoctorDataDTO> doctors
) {
}

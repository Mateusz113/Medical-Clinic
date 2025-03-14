package com.example.demo.model.facility;

import com.example.demo.model.doctor.FullDoctorDataDTO;

import java.util.List;

public record FullFacilityDataDTO(
        String name,
        String city,
        String zipCode,
        String street,
        String buildingNumber,
        List<FullDoctorDataDTO> doctors
) {
}

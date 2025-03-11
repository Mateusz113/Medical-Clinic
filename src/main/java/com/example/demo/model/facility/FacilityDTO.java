package com.example.demo.model.facility;

import com.example.demo.model.doctor.SimpleDoctorDTO;

import java.util.Set;

public record FacilityDTO(
        Long id,
        String name,
        String city,
        String zipCode,
        String street,
        String buildingNumber,
        Set<SimpleDoctorDTO> doctors
) {
}

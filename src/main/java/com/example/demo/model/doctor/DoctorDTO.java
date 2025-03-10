package com.example.demo.model.doctor;

import com.example.demo.model.facility.FacilityDTO;

import java.util.Set;

public record DoctorDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        String specialization,
        Set<FacilityDTO> facilities
) {
}

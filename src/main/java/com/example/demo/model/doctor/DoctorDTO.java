package com.example.demo.model.doctor;

import com.example.demo.model.facility.SimpleFacilityDTO;

import java.util.List;

public record DoctorDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        String specialization,
        List<SimpleFacilityDTO> facilities
) {
}

package com.example.demo.model.doctor;

import com.example.demo.model.facility.SimpleFacilityDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record DoctorDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        String specialization,
        List<SimpleFacilityDTO> facilities
) {
}

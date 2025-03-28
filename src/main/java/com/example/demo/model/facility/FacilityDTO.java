package com.example.demo.model.facility;

import com.example.demo.model.doctor.SimpleDoctorDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record FacilityDTO(
        Long id,
        String name,
        String city,
        String zipCode,
        String street,
        String buildingNumber,
        List<SimpleDoctorDTO> doctors
) {
}

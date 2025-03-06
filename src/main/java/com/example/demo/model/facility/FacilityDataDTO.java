package com.example.demo.model.facility;

public record FacilityDataDTO(
        Long id,
        String name,
        String city,
        String zipCode,
        String street,
        String buildingNumber
) {
}

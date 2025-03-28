package com.example.demo.command.facility;

import lombok.Builder;

@Builder
public record UpdateFacilityCommand(
        String name,
        String city,
        String zipCode,
        String street,
        String buildingNumber
) {
}

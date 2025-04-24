package com.example.demo.filter.visit;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record VisitFilter(
        Long visitId,
        Long doctorId,
        String doctorSpecialization,
        Long patientId,
        Boolean onlyAvailable,
        OffsetDateTime startTime,
        OffsetDateTime endTime
) {
}

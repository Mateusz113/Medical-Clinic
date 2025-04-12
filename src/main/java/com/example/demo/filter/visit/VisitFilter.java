package com.example.demo.filter.visit;

import java.time.OffsetDateTime;

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

package com.example.demo.model.visit;

import java.time.OffsetDateTime;

public record VisitCreationDTO(
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        Long doctorId
) {
}

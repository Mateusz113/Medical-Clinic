package com.example.demo.command.visit;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record InsertVisitCommand(
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        Long doctorId
) {
}

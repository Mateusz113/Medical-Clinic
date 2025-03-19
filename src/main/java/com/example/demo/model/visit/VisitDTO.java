package com.example.demo.model.visit;

import com.example.demo.model.doctor.SimpleDoctorDTO;
import com.example.demo.model.patient.PatientDTO;

import java.time.OffsetDateTime;

public record VisitDTO(
        Long id,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        SimpleDoctorDTO doctor,
        PatientDTO patient
) {
}

package com.example.demo.validator;

import com.example.demo.command.doctor.UpsertDoctorCommand;
import com.example.demo.exception.doctor.DoctorAlreadyExistsException;
import com.example.demo.exception.doctor.DoctorIllegalDataException;
import com.example.demo.model.doctor.Doctor;
import com.example.demo.repository.DoctorRepository;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

public class DoctorValidator {
    public static void validateDoctorCreation(UpsertDoctorCommand upsertDoctorCommand, DoctorRepository doctorRepository, Clock clock) {
        validateDoctorData(upsertDoctorCommand, clock);
        validateEmailAvailability(upsertDoctorCommand.email(), doctorRepository, clock);
    }

    public static void validateDoctorBulkCreation(List<UpsertDoctorCommand> upsertDoctorCommands, Clock clock) {
        upsertDoctorCommands.forEach(upsertDoctorCommand -> validateDoctorData(upsertDoctorCommand, clock));
    }

    public static void validateDoctorEdit(Doctor doctor, UpsertDoctorCommand upsertDoctorCommand, DoctorRepository doctorRepository, Clock clock) {
        validateDoctorData(upsertDoctorCommand, clock);
        if (doctorRepository.existsByEmail(upsertDoctorCommand.email()) && !Objects.equals(upsertDoctorCommand.email(), doctor.getEmail())) {
            throw new DoctorAlreadyExistsException("Doctor with email: %s already exists.".formatted(upsertDoctorCommand.email()), OffsetDateTime.now(clock));
        }
    }

    private static void validateDoctorData(UpsertDoctorCommand doctorData, Clock clock) {
        if (Objects.isNull(doctorData.email())
                || Objects.isNull(doctorData.password())
                || Objects.isNull(doctorData.firstName())
                || Objects.isNull(doctorData.lastName())
                || Objects.isNull(doctorData.specialization())) {
            throw new DoctorIllegalDataException("There cannot be null fields in doctor.", OffsetDateTime.now(clock));
        }
    }

    private static void validateEmailAvailability(String email, DoctorRepository doctorRepository, Clock clock) {
        if (doctorRepository.existsByEmail(email)) {
            throw new DoctorAlreadyExistsException("Doctor with email: %s already exists.".formatted(email), OffsetDateTime.now(clock));
        }
    }
}

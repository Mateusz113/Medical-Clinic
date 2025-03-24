package com.example.demo.validator;

import com.example.demo.command.patient.UpsertPatientCommand;
import com.example.demo.exception.patient.PatientAlreadyExistsException;
import com.example.demo.exception.patient.PatientIllegalDataException;
import com.example.demo.model.patient.Patient;
import com.example.demo.repository.PatientRepository;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;

public class PatientValidator {
    public static void validatePatientCreation(UpsertPatientCommand upsertPatientCommand, PatientRepository patientRepository, Clock clock) {
        validateFullPatientDataDTO(upsertPatientCommand, clock);
        validateEmailAvailability(upsertPatientCommand.email(), patientRepository, clock);
    }

    public static void validatePatientEdit(Patient patient, UpsertPatientCommand upsertPatientCommand, PatientRepository patientRepository, Clock clock) {
        validateFullPatientDataDTO(upsertPatientCommand, clock);
        if (patientRepository.existsByEmail(upsertPatientCommand.email()) && !Objects.equals(patient.getEmail(), upsertPatientCommand.email())) {
            throw new PatientAlreadyExistsException("Patient with email: %s already exists.".formatted(upsertPatientCommand.email()), OffsetDateTime.now(clock));
        }
        if (!patient.getIdCardNo().equals(upsertPatientCommand.idCardNo())) {
            throw new PatientIllegalDataException("ID card number cannot be changed.", OffsetDateTime.now(clock));
        }
    }

    public static void validatePatientPasswordEdit(String password, Clock clock) {
        if (password == null) {
            throw new PatientIllegalDataException("Password cannot be set to null.", OffsetDateTime.now(clock));
        }
    }

    private static void validateFullPatientDataDTO(UpsertPatientCommand upsertPatientCommand, Clock clock) {
        if (upsertPatientCommand.email() == null
                || upsertPatientCommand.password() == null
                || upsertPatientCommand.idCardNo() == null
                || upsertPatientCommand.firstName() == null
                || upsertPatientCommand.lastName() == null
                || upsertPatientCommand.phoneNumber() == null
                || upsertPatientCommand.birthday() == null) {
            throw new PatientIllegalDataException("There cannot be null fields in patient data.", OffsetDateTime.now(clock));
        }
    }

    private static void validateEmailAvailability(String email, PatientRepository patientRepository, Clock clock) {
        if (patientRepository.existsByEmail(email)) {
            throw new PatientAlreadyExistsException("Patient with email: %s already exists.".formatted(email), OffsetDateTime.now(clock));
        }
    }
}

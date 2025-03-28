package com.example.demo.validator;

import com.example.demo.command.facility.InsertFacilityCommand;
import com.example.demo.command.facility.UpdateFacilityCommand;
import com.example.demo.exception.facility.FacilityAlreadyExistsException;
import com.example.demo.exception.facility.FacilityIllegalDataException;
import com.example.demo.model.facility.Facility;
import com.example.demo.repository.FacilityRepository;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;

public class FacilityValidator {
    public static void validateFacilityCreation(InsertFacilityCommand insertFacilityCommand, FacilityRepository facilityRepository, Clock clock) {
        validateInsertFacilityCommand(insertFacilityCommand, clock);
        validateNameAvailability(insertFacilityCommand.name(), facilityRepository, clock);
        DoctorValidator.validateDoctorBulkCreation(insertFacilityCommand.doctors(), clock);
    }

    public static void validateFacilityEdit(Facility facility, UpdateFacilityCommand updateFacilityCommand, FacilityRepository facilityRepository, Clock clock) {
        validateUpdateFacilityCommand(updateFacilityCommand, clock);
        if (facilityRepository.existsByName(updateFacilityCommand.name()) && !Objects.equals(facility.getName(), updateFacilityCommand.name())) {
            throw new FacilityAlreadyExistsException("Facility with name: %s already exists.".formatted(updateFacilityCommand.name()), OffsetDateTime.now(clock));
        }
    }

    private static void validateInsertFacilityCommand(InsertFacilityCommand insertFacilityCommand, Clock clock) {
        if (Objects.isNull(insertFacilityCommand.name())
                || Objects.isNull(insertFacilityCommand.city())
                || Objects.isNull(insertFacilityCommand.zipCode())
                || Objects.isNull(insertFacilityCommand.street())
                || Objects.isNull(insertFacilityCommand.buildingNumber())
                || Objects.isNull(insertFacilityCommand.doctors())) {
            throw new FacilityIllegalDataException("There cannot be null fields in facility.", OffsetDateTime.now(clock));
        }
    }

    private static void validateUpdateFacilityCommand(UpdateFacilityCommand updateFacilityCommand, Clock clock) {
        if (Objects.isNull(updateFacilityCommand.name())
                || Objects.isNull(updateFacilityCommand.city())
                || Objects.isNull(updateFacilityCommand.zipCode())
                || Objects.isNull(updateFacilityCommand.street())
                || Objects.isNull(updateFacilityCommand.buildingNumber())) {
            throw new FacilityIllegalDataException("There cannot be null fields in facility.", OffsetDateTime.now(clock));
        }
    }

    private static void validateNameAvailability(String name, FacilityRepository facilityRepository, Clock clock) {
        if (facilityRepository.existsByName(name)) {
            throw new FacilityAlreadyExistsException("Facility with name: %s already exists.".formatted(name), OffsetDateTime.now(clock));
        }
    }
}

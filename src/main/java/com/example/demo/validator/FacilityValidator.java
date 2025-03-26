package com.example.demo.validator;

import com.example.demo.command.facility.UpsertFacilityCommand;
import com.example.demo.exception.facility.FacilityAlreadyExistsException;
import com.example.demo.exception.facility.FacilityIllegalDataException;
import com.example.demo.model.facility.Facility;
import com.example.demo.repository.FacilityRepository;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;

public class FacilityValidator {
    public static void validateFacilityCreation(UpsertFacilityCommand upsertFacilityCommand, FacilityRepository facilityRepository, Clock clock) {
        validateFacilityData(upsertFacilityCommand, clock);
        validateNameAvailability(upsertFacilityCommand.name(), facilityRepository, clock);
        DoctorValidator.validateDoctorBulkCreation(upsertFacilityCommand.doctors(), clock);
    }

    public static void validateFacilityEdit(Facility facility, UpsertFacilityCommand upsertFacilityCommand, FacilityRepository facilityRepository, Clock clock) {
        validateFacilityData(upsertFacilityCommand, clock);
        if (facilityRepository.existsByName(upsertFacilityCommand.name()) && !Objects.equals(facility.getName(), upsertFacilityCommand.name())) {
            throw new FacilityAlreadyExistsException("Facility with name: %s already exists.".formatted(upsertFacilityCommand.name()), OffsetDateTime.now(clock));
        }
    }

    private static void validateFacilityData(UpsertFacilityCommand upsertFacilityCommand, Clock clock) {
        if (Objects.isNull(upsertFacilityCommand.name())
                || Objects.isNull(upsertFacilityCommand.city())
                || Objects.isNull(upsertFacilityCommand.zipCode())
                || Objects.isNull(upsertFacilityCommand.street())
                || Objects.isNull(upsertFacilityCommand.buildingNumber())) {
            throw new FacilityIllegalDataException("There cannot be null fields in facility.", OffsetDateTime.now(clock));
        }
    }

    private static void validateNameAvailability(String name, FacilityRepository facilityRepository, Clock clock) {
        if (facilityRepository.existsByName(name)) {
            throw new FacilityAlreadyExistsException("Facility with name: %s already exists.".formatted(name), OffsetDateTime.now(clock));
        }
    }
}

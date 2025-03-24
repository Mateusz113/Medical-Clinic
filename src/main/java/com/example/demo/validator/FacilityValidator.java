package com.example.demo.validator;

import com.example.demo.exception.facility.FacilityAlreadyExistsException;
import com.example.demo.exception.facility.FacilityIllegalDataException;
import com.example.demo.model.facility.Facility;
import com.example.demo.model.facility.FullFacilityDataDTO;
import com.example.demo.repository.FacilityRepository;

import java.time.OffsetDateTime;
import java.util.Objects;

public class FacilityValidator {
    public static void validateFacilityCreation(FullFacilityDataDTO facilityData, FacilityRepository facilityRepository) {
        validateFacilityData(facilityData);
        validateNameAvailability(facilityData.name(), facilityRepository);
        DoctorValidator.validateDoctorBulkCreation(facilityData.doctors());
    }

    public static void validateFacilityEdit(Facility facility, FullFacilityDataDTO facilityData, FacilityRepository facilityRepository) {
        validateFacilityData(facilityData);
        if (facilityRepository.existsByName(facilityData.name()) && !Objects.equals(facility.getName(), facilityData.name())) {
            throw new FacilityAlreadyExistsException("Facility with name: %s already exists.".formatted(facilityData.name()), OffsetDateTime.now());
        }
    }

    private static void validateFacilityData(FullFacilityDataDTO facilityData) {
        if (Objects.isNull(facilityData.name())
                || Objects.isNull(facilityData.city())
                || Objects.isNull(facilityData.zipCode())
                || Objects.isNull(facilityData.street())
                || Objects.isNull(facilityData.buildingNumber())) {
            throw new FacilityIllegalDataException("There cannot be null fields in facility.", OffsetDateTime.now());
        }
    }

    private static void validateNameAvailability(String name, FacilityRepository facilityRepository) {
        if (facilityRepository.existsByName(name)) {
            throw new FacilityAlreadyExistsException("Facility with name: %s already exists.".formatted(name), OffsetDateTime.now());
        }
    }
}

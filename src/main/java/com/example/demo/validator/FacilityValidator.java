package com.example.demo.validator;

import com.example.demo.exception.facility.FacilityAlreadyExistsException;
import com.example.demo.exception.facility.FacilityIllegalArgumentException;
import com.example.demo.model.facility.Facility;
import com.example.demo.model.facility.FullFacilityDataDTO;
import com.example.demo.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FacilityValidator {
    private final FacilityRepository facilityRepository;
    private final DoctorValidator doctorValidator;

    public void validateFacilityCreation(FullFacilityDataDTO facilityData) {
        validateFacilityData(facilityData);
        validateNameAvailability(facilityData.name());
        facilityData.doctors().forEach(doctorValidator::validateDoctorCreation);
    }

    public void validateFacilityEdit(Facility facility, FullFacilityDataDTO facilityData) {
        validateFacilityData(facilityData);
        if (facilityRepository.existsByName(facilityData.name()) && !Objects.equals(facility.getName(), facilityData.name())) {
            throw new FacilityAlreadyExistsException("Facility with name: %s already exists.".formatted(facilityData.name()), OffsetDateTime.now());
        }
    }

    private void validateFacilityData(FullFacilityDataDTO facilityData) {
        if (facilityData.name() == null
                || facilityData.city() == null
                || facilityData.zipCode() == null
                || facilityData.street() == null
                || facilityData.buildingNumber() == null) {
            throw new FacilityIllegalArgumentException("There cannot be null fields in facility.", OffsetDateTime.now());
        }
    }

    private void validateNameAvailability(String name) {
        if (facilityRepository.existsByName(name)) {
            throw new FacilityAlreadyExistsException("Facility with name: %s already exists.".formatted(name), OffsetDateTime.now());
        }
    }
}

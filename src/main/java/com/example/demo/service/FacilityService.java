package com.example.demo.service;

import com.example.demo.exception.facility.FacilityAlreadyExistsException;
import com.example.demo.exception.facility.FacilityIllegalArgumentException;
import com.example.demo.exception.facility.FacilityNotFoundException;
import com.example.demo.mapper.FacilityMapper;
import com.example.demo.model.facility.Facility;
import com.example.demo.model.facility.FacilityDTO;
import com.example.demo.model.facility.FullFacilityDataDTO;
import com.example.demo.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final FacilityMapper facilityMapper;

    public FacilityDTO createFacility(FullFacilityDataDTO facilityData) {
        validateFacilityCreation(facilityData);
        Facility facility = facilityRepository.save(facilityMapper.toEntity(facilityData));
        return facilityMapper.toDTO(facility);
    }

    public List<FacilityDTO> getFacilities() {
        return facilityRepository.findAll().stream()
                .map(facilityMapper::toDTO)
                .toList();
    }

    public FacilityDTO getFacilityById(Long id) {
        Facility facility = getFacilityWithId(id);
        return facilityMapper.toDTO(facility);
    }

    public FacilityDTO editFacility(Long id, FullFacilityDataDTO facilityData) {
        Facility facility = getFacilityWithId(id);
        validateFacilityEdit(facility, facilityData);
        facility.update(facilityData);
        facilityRepository.save(facility);
        return facilityMapper.toDTO(facility);
    }

    public void deleteFacility(Long id) {
        Facility facility = getFacilityWithId(id);
        facilityRepository.delete(facility);
    }

    private Facility getFacilityWithId(Long id) {
        return facilityRepository.findById(id)
                .orElseThrow(() -> new FacilityNotFoundException("Facility with id: %d does not exist.".formatted(id), OffsetDateTime.now()));
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

    private void validateFacilityEdit(Facility facility, FullFacilityDataDTO facilityData) {
        validateFacilityData(facilityData);
        if (facilityRepository.existsByName(facilityData.name()) && !Objects.equals(facility.getName(), facilityData.name())) {
            throw new FacilityAlreadyExistsException("Facility with name: %s already exists.".formatted(facilityData.name()), OffsetDateTime.now());
        }
    }

    private void validateFacilityCreation(FullFacilityDataDTO facilityData) {
        validateFacilityData(facilityData);
        validateNameAvailability(facilityData.name());
    }
}

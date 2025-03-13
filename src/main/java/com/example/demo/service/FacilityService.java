package com.example.demo.service;

import com.example.demo.exception.facility.FacilityNotFoundException;
import com.example.demo.mapper.FacilityMapper;
import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.doctor.FullDoctorDataDTO;
import com.example.demo.model.facility.Facility;
import com.example.demo.model.facility.FacilityDTO;
import com.example.demo.model.facility.FullFacilityDataDTO;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.FacilityRepository;
import com.example.demo.validator.FacilityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final DoctorRepository doctorRepository;
    private final FacilityMapper facilityMapper;
    private final FacilityValidator facilityValidator;

    @Transactional
    public FacilityDTO createFacility(FullFacilityDataDTO facilityData) {
        Facility facility = saveFacilityToDatabase(facilityData);
        return facilityMapper.toDTO(facility);
    }

    @Transactional
    public List<FacilityDTO> createFacilities(List<FullFacilityDataDTO> facilityDataList) {
        List<Facility> facilities = facilityDataList.stream()
                .map(this::saveFacilityToDatabase)
                .toList();
        return facilities.stream()
                .map(facilityMapper::toDTO)
                .toList();
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

    @Transactional
    public FacilityDTO editFacility(Long id, FullFacilityDataDTO facilityData) {
        Facility facility = getFacilityWithId(id);
        facilityValidator.validateFacilityEdit(facility, facilityData);
        facility.update(facilityData);
        facilityRepository.save(facility);
        return facilityMapper.toDTO(facility);
    }

    @Transactional
    public void deleteFacility(Long id) {
        Facility facility = getFacilityWithId(id);
        facilityRepository.delete(facility);
    }

    private Facility getFacilityWithId(Long id) {
        return facilityRepository.findById(id)
                .orElseThrow(() -> new FacilityNotFoundException("Facility with id: %d does not exist.".formatted(id), OffsetDateTime.now()));
    }

    private Facility saveFacilityToDatabase(FullFacilityDataDTO facilityData) {
        Set<Doctor> existingDoctors = getExistingDoctors(facilityData);
        facilityData = filterNewDoctorData(facilityData, existingDoctors);
        facilityValidator.validateFacilityCreation(facilityData);
        Facility facility = facilityMapper.toEntity(facilityData);
        Set<Doctor> doctors = facility.getDoctors();
        doctors.addAll(existingDoctors);
        doctors.forEach((doctor -> doctor.addFacility(facility)));
        return facilityRepository.save(facility);
    }

    private Set<Doctor> getExistingDoctors(FullFacilityDataDTO facilityDataDTO) {
        List<String> requestDoctorEmails = facilityDataDTO.doctors().stream()
                .map(FullDoctorDataDTO::email)
                .toList();
        return new HashSet<>(doctorRepository.findAllByEmails(requestDoctorEmails));
    }

    private FullFacilityDataDTO filterNewDoctorData(FullFacilityDataDTO facilityDataDTO, Set<Doctor> existingDoctors) {
        List<String> existingDoctorEmails = existingDoctors.stream()
                .map(Doctor::getEmail)
                .toList();
        Set<FullDoctorDataDTO> filteredNewDoctorData = facilityDataDTO.doctors().stream()
                .filter(doctorData -> !existingDoctorEmails.contains(doctorData.email()))
                .collect(Collectors.toSet());
        return new FullFacilityDataDTO(
                facilityDataDTO.name(),
                facilityDataDTO.city(),
                facilityDataDTO.zipCode(),
                facilityDataDTO.street(),
                facilityDataDTO.buildingNumber(),
                filteredNewDoctorData
        );
    }
}

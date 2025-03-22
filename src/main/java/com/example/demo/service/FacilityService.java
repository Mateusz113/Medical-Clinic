package com.example.demo.service;

import com.example.demo.exception.facility.FacilityNotFoundException;
import com.example.demo.mapper.DoctorMapper;
import com.example.demo.mapper.FacilityMapper;
import com.example.demo.model.PageableContentDto;
import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.doctor.FullDoctorDataDTO;
import com.example.demo.model.facility.Facility;
import com.example.demo.model.facility.FacilityDTO;
import com.example.demo.model.facility.FullFacilityDataDTO;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.FacilityRepository;
import com.example.demo.validator.FacilityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final DoctorRepository doctorRepository;
    private final FacilityMapper facilityMapper;
    private final DoctorMapper doctorMapper;

    @Transactional
    public FacilityDTO createFacility(FullFacilityDataDTO facilityData) {
        Facility facility = saveFacilityToDatabase(facilityData);
        return facilityMapper.toDTO(facility);
    }

    @Transactional
    public List<FacilityDTO> createFacilities(List<FullFacilityDataDTO> facilityDataList) {
        List<Facility> facilities = new LinkedList<>();
        facilityDataList.forEach(facilityData -> {
            Facility facility = saveFacilityToDatabase(facilityData);
            facilities.add(facility);
        });
        return facilityMapper.toDTOs(facilities);
    }

    public PageableContentDto<FacilityDTO> getFacilities(Pageable pageable) {
        return getAllFacilitiesWithPageable(pageable);
    }

    public FacilityDTO getFacilityById(Long id) {
        Facility facility = getFacilityWithId(id);
        return facilityMapper.toDTO(facility);
    }

    @Transactional
    public FacilityDTO editFacility(Long id, FullFacilityDataDTO facilityData) {
        Facility facility = getFacilityWithId(id);
        FacilityValidator.validateFacilityEdit(facility, facilityData, facilityRepository);
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

    private PageableContentDto<FacilityDTO> getAllFacilitiesWithPageable(Pageable pageable) {
        Page<Facility> content = facilityRepository.findAll(pageable);
        return PageableContentDto.<FacilityDTO>builder()
                .totalEntries(content.getTotalElements())
                .totalNumberOfPages(content.getTotalPages())
                .pageNumber(pageable.getPageNumber())
                .content(content.stream().map(facilityMapper::toDTO).toList())
                .build();
    }

    private Facility saveFacilityToDatabase(FullFacilityDataDTO facilityData) {
        FacilityValidator.validateFacilityCreation(facilityData, facilityRepository);
        Set<Doctor> allDoctors = getExistingDoctors(facilityData);
        addMissingDoctors(facilityData, allDoctors);
        Facility facility = facilityMapper.toEntity(facilityData);
        allDoctors.forEach((doctor -> doctor.addFacility(facility)));
        facility.setDoctors(allDoctors);
        return facilityRepository.save(facility);
    }

    private Set<Doctor> getExistingDoctors(FullFacilityDataDTO facilityDataDTO) {
        List<String> requestDoctorEmails = facilityDataDTO.doctors().stream()
                .map(FullDoctorDataDTO::email)
                .toList();
        return new HashSet<>(doctorRepository.findAllByEmails(requestDoctorEmails));
    }

    private void addMissingDoctors(FullFacilityDataDTO facilityDataDTO, Set<Doctor> existingDoctors) {
        Set<FullDoctorDataDTO> missingDoctors = getMissingDoctors(existingDoctors, facilityDataDTO.doctors());
        existingDoctors.addAll(doctorMapper.toEntities(missingDoctors));
    }

    private Set<FullDoctorDataDTO> getMissingDoctors(Set<Doctor> existingDoctors, List<FullDoctorDataDTO> doctors) {
        List<String> existingDoctorEmails = existingDoctors.stream()
                .map(Doctor::getEmail)
                .toList();
        return doctors.stream()
                .filter(doctorData -> !existingDoctorEmails.contains(doctorData.email()))
                .collect(Collectors.toSet());
    }
}

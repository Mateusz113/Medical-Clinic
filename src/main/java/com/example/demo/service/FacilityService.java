package com.example.demo.service;

import com.example.demo.command.doctor.UpsertDoctorCommand;
import com.example.demo.command.facility.UpsertFacilityCommand;
import com.example.demo.exception.facility.FacilityNotFoundException;
import com.example.demo.mapper.DoctorMapper;
import com.example.demo.mapper.FacilityMapper;
import com.example.demo.model.PageableContentDto;
import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.facility.Facility;
import com.example.demo.model.facility.FacilityDTO;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.FacilityRepository;
import com.example.demo.validator.FacilityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
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
    private final Clock clock;

    @Transactional
    public FacilityDTO createFacility(UpsertFacilityCommand upsertFacilityCommand) {
        Facility facility = saveFacilityToDatabase(upsertFacilityCommand);
        return facilityMapper.toDTO(facility);
    }

    @Transactional
    public List<FacilityDTO> createFacilities(List<UpsertFacilityCommand> upsertFacilityCommands) {
        List<Facility> facilities = new LinkedList<>();
        upsertFacilityCommands.forEach(upsertFacilityCommand -> {
            Facility facility = saveFacilityToDatabase(upsertFacilityCommand);
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
    public FacilityDTO editFacility(Long id, UpsertFacilityCommand upsertFacilityCommand) {
        Facility facility = getFacilityWithId(id);
        FacilityValidator.validateFacilityEdit(facility, upsertFacilityCommand, facilityRepository, clock);
        facility.update(upsertFacilityCommand);
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
                .orElseThrow(() -> new FacilityNotFoundException("Facility with id: %d does not exist.".formatted(id), OffsetDateTime.now(clock)));
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

    private Facility saveFacilityToDatabase(UpsertFacilityCommand upsertFacilityCommand) {
        FacilityValidator.validateFacilityCreation(upsertFacilityCommand, facilityRepository, clock);
        Set<Doctor> allDoctors = getExistingDoctors(upsertFacilityCommand);
        addMissingDoctors(upsertFacilityCommand, allDoctors);
        Facility facility = facilityMapper.toEntity(upsertFacilityCommand);
        allDoctors.forEach((doctor -> doctor.addFacility(facility)));
        facility.setDoctors(allDoctors);
        return facilityRepository.save(facility);
    }

    private Set<Doctor> getExistingDoctors(UpsertFacilityCommand upsertFacilityCommand) {
        List<String> requestDoctorEmails = upsertFacilityCommand.doctors().stream()
                .map(UpsertDoctorCommand::email)
                .toList();
        return new HashSet<>(doctorRepository.findAllByEmails(requestDoctorEmails));
    }

    private void addMissingDoctors(UpsertFacilityCommand upsertFacilityCommand, Set<Doctor> existingDoctors) {
        Set<UpsertDoctorCommand> missingDoctors = getMissingDoctors(existingDoctors, upsertFacilityCommand.doctors());
        existingDoctors.addAll(doctorMapper.toEntities(missingDoctors));
    }

    private Set<UpsertDoctorCommand> getMissingDoctors(Set<Doctor> existingDoctors, List<UpsertDoctorCommand> upsertDoctorCommands) {
        List<String> existingDoctorEmails = existingDoctors.stream()
                .map(Doctor::getEmail)
                .toList();
        return upsertDoctorCommands.stream()
                .filter(doctorData -> !existingDoctorEmails.contains(doctorData.email()))
                .collect(Collectors.toSet());
    }
}

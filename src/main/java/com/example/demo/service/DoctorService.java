package com.example.demo.service;

import com.example.demo.exception.doctor.DoctorNotFoundException;
import com.example.demo.exception.facility.FacilityNotFoundException;
import com.example.demo.mapper.DoctorMapper;
import com.example.demo.model.PageableContentDto;
import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.doctor.DoctorDTO;
import com.example.demo.model.doctor.FullDoctorDataDTO;
import com.example.demo.model.facility.Facility;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.FacilityRepository;
import com.example.demo.validator.DoctorValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final FacilityRepository facilityRepository;
    private final DoctorMapper doctorMapper;
    private final DoctorValidator doctorValidator;

    @Transactional
    public DoctorDTO createDoctor(FullDoctorDataDTO doctorData) {
        doctorValidator.validateDoctorCreation(doctorData);
        Doctor doctor = doctorRepository.save(doctorMapper.toEntity(doctorData));
        return doctorMapper.toDTO(doctor);
    }

    public PageableContentDto<DoctorDTO> getDoctors(Pageable pageable) {
        return getAllDoctorsWithPageable(pageable);
    }

    public DoctorDTO getDoctorByEmail(String email) {
        Doctor doctor = getDoctorWithEmail(email);
        return doctorMapper.toDTO(doctor);
    }

    @Transactional
    public DoctorDTO editDoctor(String email, FullDoctorDataDTO doctorData) {
        Doctor doctor = getDoctorWithEmail(email);
        doctorValidator.validateDoctorEdit(doctor, doctorData);
        doctor.update(doctorData);
        doctorRepository.save(doctor);
        return doctorMapper.toDTO(doctor);
    }

    @Transactional
    public void updateFacilities(String email, List<Long> facilitiesIds) {
        Doctor doctor = getDoctorWithEmail(email);
        Set<Facility> facilities = getFacilitiesWithIds(facilitiesIds);
        doctor.setFacilities(facilities);
        doctorRepository.save(doctor);
    }

    @Transactional
    public void deleteDoctor(String email) {
        Doctor doctor = getDoctorWithEmail(email);
        doctorRepository.delete(doctor);
    }

    private Doctor getDoctorWithEmail(String email) {
        return doctorRepository.findByEmail(email)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with email: %s does not exist.".formatted(email), OffsetDateTime.now()));
    }

    private PageableContentDto<DoctorDTO> getAllDoctorsWithPageable(Pageable pageable) {
        Page<Doctor> content = doctorRepository.findAll(pageable);
        return PageableContentDto.<DoctorDTO>builder()
                .totalEntries(content.getTotalElements())
                .totalNumberOfPages(content.getTotalPages())
                .pageNumber(pageable.getPageNumber())
                .content(content.stream().map(doctorMapper::toDTO).toList())
                .build();
    }

    private Set<Facility> getFacilitiesWithIds(List<Long> facilitiesIds) {
        Set<Facility> facilities = new HashSet<>(facilityRepository.findFacilitiesByIds(facilitiesIds));
        if (facilities.size() != facilitiesIds.size()) {
            throw new FacilityNotFoundException("List of facilities ids contained invalid values.", OffsetDateTime.now());
        }
        return facilities;
    }
}

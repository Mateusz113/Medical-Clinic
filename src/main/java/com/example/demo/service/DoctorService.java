package com.example.demo.service;

import com.example.demo.exception.doctor.DoctorAlreadyExistsException;
import com.example.demo.exception.doctor.DoctorFacilityContractViolationException;
import com.example.demo.exception.doctor.DoctorIllegalArgumentException;
import com.example.demo.exception.doctor.DoctorNotFoundException;
import com.example.demo.exception.facility.FacilityNotFoundException;
import com.example.demo.exception.patient.PatientAlreadyExistsException;
import com.example.demo.mapper.DoctorMapper;
import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.doctor.DoctorDTO;
import com.example.demo.model.doctor.FullDoctorDataDTO;
import com.example.demo.model.facility.Facility;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final FacilityRepository facilityRepository;
    private final DoctorMapper doctorMapper;

    @Transactional
    public DoctorDTO createDoctor(FullDoctorDataDTO doctorData) {
        validateDoctorCreation(doctorData);
        Doctor doctor = doctorRepository.save(doctorMapper.toEntity(doctorData));
        return doctorMapper.toDTO(doctor);
    }

    public List<DoctorDTO> getDoctors() {
        return doctorRepository.findAll().stream()
                .map(doctorMapper::toDTO)
                .toList();
    }

    public DoctorDTO getDoctorByEmail(String email) {
        Doctor doctor = getDoctorWithEmail(email);
        return doctorMapper.toDTO(doctor);
    }

    @Transactional
    public DoctorDTO editDoctor(String email, FullDoctorDataDTO doctorData) {
        Doctor doctor = getDoctorWithEmail(email);
        validateDoctorEdit(doctor, doctorData);
        doctor.update(doctorData);
        doctorRepository.save(doctor);
        return doctorMapper.toDTO(doctor);
    }

    @Transactional
    public void addFacility(String email, Long id) {
        updateFacilitiesSet(email, id, Doctor::addFacility);
    }

    @Transactional
    public void removeFacility(String email, Long id) {
        updateFacilitiesSet(email, id, Doctor::removeFacility);
    }

    @Transactional
    public void deleteDoctor(String email) {
        Doctor doctor = getDoctorWithEmail(email);
        doctorRepository.delete(doctor);
    }

    private void updateFacilitiesSet(String doctorEmail,
                                     Long facilityId,
                                     BiFunction<Doctor, Facility, Boolean> facilitySetOperation) {
        Doctor doctor = getDoctorWithEmail(doctorEmail);
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new FacilityNotFoundException("Facility with id: %d does not exist.".formatted(facilityId), OffsetDateTime.now()));
        boolean operationSuccessful = facilitySetOperation.apply(doctor, facility);
        if (!operationSuccessful) {
            throw new DoctorFacilityContractViolationException("There was an error updating information about doctor and facility relation.", OffsetDateTime.now());
        }
        doctorRepository.save(doctor);
    }

    private Doctor getDoctorWithEmail(String email) {
        return doctorRepository.findByEmail(email)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with email: %s does not exist.".formatted(email), OffsetDateTime.now()));
    }

    private void validateDoctorData(FullDoctorDataDTO doctorData) {
        if (doctorData.email() == null
                || doctorData.password() == null
                || doctorData.firstName() == null
                || doctorData.lastName() == null
                || doctorData.specialization() == null) {
            throw new DoctorIllegalArgumentException("There cannot be null fields in doctor.", OffsetDateTime.now());
        }
    }

    private void validateEmailAvailability(String email) {
        if (doctorRepository.existsByEmail(email)) {
            throw new DoctorAlreadyExistsException("Doctor with email: %s already exists.".formatted(email), OffsetDateTime.now());
        }
    }

    private void validateDoctorEdit(Doctor doctor, FullDoctorDataDTO doctorData) {
        validateDoctorData(doctorData);
        if (doctorRepository.existsByEmail(doctorData.email()) && !Objects.equals(doctorData.email(), doctor.getEmail())) {
            throw new PatientAlreadyExistsException("Doctor with email: %s already exists.".formatted(doctorData.email()), OffsetDateTime.now());
        }
    }

    private void validateDoctorCreation(FullDoctorDataDTO doctorData) {
        validateDoctorData(doctorData);
        validateEmailAvailability(doctorData.email());
    }
}

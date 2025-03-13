package com.example.demo.service;

import com.example.demo.exception.doctor.DoctorFacilityContractViolationException;
import com.example.demo.exception.doctor.DoctorNotFoundException;
import com.example.demo.exception.facility.FacilityNotFoundException;
import com.example.demo.mapper.DoctorMapper;
import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.doctor.DoctorDTO;
import com.example.demo.model.doctor.FullDoctorDataDTO;
import com.example.demo.model.facility.Facility;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.FacilityRepository;
import com.example.demo.validator.DoctorValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.BiFunction;

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

    public List<DoctorDTO> getDoctors(PageRequest pageRequest) {
        return doctorRepository.findAll(pageRequest).stream()
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
        doctorValidator.validateDoctorEdit(doctor, doctorData);
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
}

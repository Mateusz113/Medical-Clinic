package com.example.demo.service;

import com.example.demo.exception.doctor.DoctorIllegalArgumentException;
import com.example.demo.exception.doctor.DoctorNotFoundException;
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

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final DoctorRepository doctorRepository;
    private final FacilityMapper facilityMapper;
    private final FacilityValidator facilityValidator;

    public FacilityDTO createFacility(FullFacilityDataDTO facilityData) {
        Facility facility = saveFacilityToDatabase(facilityData);
        return facilityMapper.toDTO(facility);
    }

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

    public FacilityDTO editFacility(Long id, FullFacilityDataDTO facilityData) {
        Facility facility = getFacilityWithId(id);
        facilityValidator.validateFacilityEdit(facility, facilityData);
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

    private Facility saveFacilityToDatabase(FullFacilityDataDTO facilityData) {
        Set<Doctor> existingDoctors = getExistingDoctors(facilityData);
        facilityValidator.validateFacilityCreation(facilityData);
        Facility facility = facilityMapper.toEntity(facilityData);
        Set<Doctor> doctors = facility.getDoctors();
        doctors.addAll(existingDoctors);
        doctors.forEach((doctor -> doctor.addFacility(facility)));
        return facilityRepository.save(facility);
    }

    private Set<Doctor> getExistingDoctors(FullFacilityDataDTO facilityDataDTO) {
        Set<Doctor> existingDoctors = new HashSet<>();
        Iterator<FullDoctorDataDTO> iterator = facilityDataDTO.doctors().iterator();
        while (iterator.hasNext()) {
            FullDoctorDataDTO doctorData = iterator.next();
            if (doctorRepository.existsByEmail(doctorData.email())) {
                Doctor existingDoctor = doctorRepository.findByEmail(doctorData.email())
                        .orElseThrow(() -> new DoctorNotFoundException("Doctor with email: %s does not exist.".formatted(doctorData.email()), OffsetDateTime.now()));
                if (!Objects.equals(doctorData.firstName(), existingDoctor.getFirstName())
                        || !Objects.equals(doctorData.lastName(), existingDoctor.getLastName())
                        || !Objects.equals(doctorData.specialization(), existingDoctor.getSpecialization())
                        || !Objects.equals(doctorData.password(), existingDoctor.getPassword())) {
                    throw new DoctorIllegalArgumentException("Provided a doctor data with occupied email address and data that does not match persistence.", OffsetDateTime.now());
                }
                existingDoctors.add(existingDoctor);
                iterator.remove();
            }
        }
        return existingDoctors;
    }
}

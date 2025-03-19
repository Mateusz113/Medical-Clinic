package com.example.demo.service;

import com.example.demo.exception.doctor.DoctorNotFoundException;
import com.example.demo.exception.patient.PatientNotFoundException;
import com.example.demo.exception.visit.VisitNotFoundException;
import com.example.demo.mapper.VisitMapper;
import com.example.demo.model.PageableContentDto;
import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.patient.Patient;
import com.example.demo.model.visit.Visit;
import com.example.demo.model.visit.VisitCreationDTO;
import com.example.demo.model.visit.VisitDTO;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.VisitRepository;
import com.example.demo.validator.VisitValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public VisitDTO createVisit(VisitCreationDTO visitData) {
        VisitValidator.validateVisitData(visitData, visitRepository, doctorRepository);
        Visit visit = visitMapper.toEntity(visitData);
        Doctor doctor = getDoctorWithId(visitData.doctorId());
        visit.setDoctor(doctor);
        visit = visitRepository.save(visit);
        return visitMapper.toDto(visit);
    }

    public PageableContentDto<VisitDTO> getAllVisits(Pageable pageable) {
        Page<Visit> visits = visitRepository.findAll(pageable);
        return createPageableContentDto(visits, pageable);
    }

    public PageableContentDto<VisitDTO> getDoctorVisits(Long doctorId, Pageable pageable) {
        Page<Visit> visits = visitRepository.findAllByDoctorId(doctorId, pageable);
        return createPageableContentDto(visits, pageable);
    }

    public PageableContentDto<VisitDTO> getPatientVisits(Long patientId, Pageable pageable) {
        Page<Visit> visits = visitRepository.findAllByPatientId(patientId, pageable);
        return createPageableContentDto(visits, pageable);
    }

    @Transactional
    public void registerPatientToVisit(Long visitId, Long patientId) {
        Visit visit = getVisitWithId(visitId);
        Patient patient = getPatientWithId(patientId);
        VisitValidator.validateVisitAvailability(visit);
        visit.setPatient(patient);
        visitRepository.save(visit);
    }

    @Transactional
    public void deleteVisit(Long visitId) {
        Visit visit = getVisitWithId(visitId);
        visitRepository.delete(visit);
    }

    private Doctor getDoctorWithId(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with id: %d does not exist.".formatted(doctorId), OffsetDateTime.now()));
    }

    private Visit getVisitWithId(Long visitId) {
        return visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitNotFoundException("Visit with id: %d does not exist.".formatted(visitId), OffsetDateTime.now()));
    }

    private Patient getPatientWithId(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient with id: %d does not exist.".formatted(patientId), OffsetDateTime.now()));
    }

    private PageableContentDto<VisitDTO> createPageableContentDto(Page<Visit> visits, Pageable pageable) {
        return PageableContentDto.<VisitDTO>builder()
                .totalEntries(visits.getTotalElements())
                .totalNumberOfPages(visits.getTotalPages())
                .pageNumber(pageable.getPageNumber())
                .content(visits.get().map(visitMapper::toDto).toList())
                .build();
    }
}

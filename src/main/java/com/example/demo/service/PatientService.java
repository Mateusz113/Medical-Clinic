package com.example.demo.service;

import com.example.demo.command.patient.UpsertPatientCommand;
import com.example.demo.exception.patient.PatientNotFoundException;
import com.example.demo.mapper.PatientMapper;
import com.example.demo.model.PageableContentDto;
import com.example.demo.model.patient.Patient;
import com.example.demo.model.patient.PatientDTO;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.VisitRepository;
import com.example.demo.validator.PatientValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final VisitRepository visitRepository;
    private final Clock clock;

    @Transactional
    public PatientDTO createPatient(UpsertPatientCommand upsertPatientCommand) {
        PatientValidator.validatePatientCreation(upsertPatientCommand, patientRepository, clock);
        Patient patient = patientRepository.save(patientMapper.toEntity(upsertPatientCommand));
        return patientMapper.toDTO(patient);
    }

    public PageableContentDto<PatientDTO> getAllPatients(Pageable pageable) {
        return getAllPatientsWithPageable(pageable);
    }

    public PatientDTO getPatient(String email) {
        Patient patient = getPatientByEmail(email);
        return patientMapper.toDTO(patient);
    }

    @Transactional
    public PatientDTO editPatient(String email, UpsertPatientCommand upsertPatientCommand) {
        Patient patient = getPatientByEmail(email);
        PatientValidator.validatePatientEdit(patient, upsertPatientCommand, patientRepository, clock);
        patient.update(upsertPatientCommand);
        patientRepository.save(patient);
        return patientMapper.toDTO(patient);
    }

    @Transactional
    public void editPatientPassword(String email, String password) {
        PatientValidator.validatePatientPasswordEdit(password, clock);
        Patient patient = getPatientByEmail(email);
        patient.setPassword(password);
        patientRepository.save(patient);
    }

    @Transactional
    public void deletePatient(String email) {
        Patient patient = getPatientByEmail(email);
        visitRepository.detachPatientIdFromVisits(patient.getId());
        patientRepository.delete(patient);
    }

    private PageableContentDto<PatientDTO> getAllPatientsWithPageable(Pageable pageable) {
        Page<Patient> content = patientRepository.findAll(pageable);
        return PageableContentDto.<PatientDTO>builder()
                .totalEntries(content.getTotalElements())
                .totalNumberOfPages(content.getTotalPages())
                .pageNumber(pageable.getPageNumber())
                .content(content.stream().map(patientMapper::toDTO).toList())
                .build();
    }

    private Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient with email: %s does not exist.".formatted(email), OffsetDateTime.now(clock)));
    }
}

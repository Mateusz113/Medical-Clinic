package com.example.demo.service;

import com.example.demo.exception.PatientAlreadyExistsException;
import com.example.demo.exception.PatientIllegalArgumentException;
import com.example.demo.exception.PatientNotFoundException;
import com.example.demo.exception.PatientOperationException;
import com.example.demo.mapper.PatientMapper;
import com.example.demo.model.FullPatientDataDTO;
import com.example.demo.model.Patient;
import com.example.demo.model.PatientDTO;
import com.example.demo.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableJpaRepositories(basePackages = "com.example.demo.repository")
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(patientMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PatientDTO getPatient(String email) {
        return patientMapper.toDTO(patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient with email: %s does not exist.".formatted(email), OffsetDateTime.now())));
    }

    @Transactional
    public PatientDTO createPatient(FullPatientDataDTO patientData) {
        if (isAnyPatientFieldNull(patientData)) {
            throw new PatientIllegalArgumentException("There cannot be null fields in patient data.", OffsetDateTime.now());
        }
        try {
            return patientMapper.toDTO(patientRepository.save(patientMapper.toEntity(patientData)));
        } catch (DataIntegrityViolationException e) {
            throw new PatientAlreadyExistsException("Patient with email: %s already exists".formatted(patientData.email()), OffsetDateTime.now());
        }
    }

    @Transactional
    public void deletePatient(String email) {
        if (patientRepository.deleteByEmail(email) == 0) {
            throw new PatientNotFoundException("Patient with email: %s does not exist.".formatted(email), OffsetDateTime.now());
        }
    }

    @Transactional
    public PatientDTO editPatient(String email, FullPatientDataDTO patientData) {
        if (isAnyPatientFieldNull(patientData)) {
            throw new PatientIllegalArgumentException("There cannot be null fields in patient data.", OffsetDateTime.now());
        }
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient with email %s does not exist.".formatted(email), OffsetDateTime.now()));
        if (!patient.getIdCardNo().equals(patientData.idCardNo())) {
            throw new PatientIllegalArgumentException("ID card number cannot be changed.", OffsetDateTime.now());
        }
        try {
            if (patientRepository.update(email, patientMapper.toEntity(patientData)) > 0) {
                return patientRepository.findByEmail(patientData.email()).map(patientMapper::toDTO)
                        .orElseThrow(() -> new PatientNotFoundException("Patient with email %s does not exist.", OffsetDateTime.now()));
            } else {
                throw new PatientOperationException("Something went wrong when updating patient with email: %s.", OffsetDateTime.now());
            }
        } catch (DataIntegrityViolationException e) {
            throw new PatientAlreadyExistsException("Patient with email: %s already exists".formatted(patientData.email()), OffsetDateTime.now());
        }
    }

    @Transactional
    public PatientDTO editPatientPassword(String email, String password) {
        if (password == null) {
            throw new PatientIllegalArgumentException("Password cannot be set to null.", OffsetDateTime.now());
        }
        if (patientRepository.updatePassword(email, password) > 0) {
            return patientRepository.findByEmail(email).map(patientMapper::toDTO)
                    .orElseThrow(() -> new PatientNotFoundException("Patient with email %s does not exist.".formatted(email), OffsetDateTime.now()));
        } else {
            throw new PatientOperationException("Something went wrong when updating patient with email: %s.".formatted(email), OffsetDateTime.now());
        }
    }

    private boolean isAnyPatientFieldNull(FullPatientDataDTO patientData) {
        return patientData.email() == null
                || patientData.password() == null
                || patientData.idCardNo() == null
                || patientData.firstName() == null
                || patientData.lastName() == null
                || patientData.phoneNumber() == null
                || patientData.birthday() == null;
    }
}

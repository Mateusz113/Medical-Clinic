package com.example.demo.service;

import com.example.demo.exception.patient.PatientAlreadyExistsException;
import com.example.demo.exception.patient.PatientIllegalArgumentException;
import com.example.demo.exception.patient.PatientNotFoundException;
import com.example.demo.mapper.PatientMapper;
import com.example.demo.model.patient.FullPatientDataDTO;
import com.example.demo.model.patient.Patient;
import com.example.demo.model.patient.PatientDTO;
import com.example.demo.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(patientMapper::toDTO)
                .toList();
    }

    public PatientDTO getPatient(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient with email: %s does not exist.".formatted(email), OffsetDateTime.now()));
        return patientMapper.toDTO(patient);
    }

    @Transactional
    public PatientDTO createPatient(FullPatientDataDTO patientData) {
        validateFullPatientDataDTO(patientData);
        validateEmailAvailability(patientData.email());
        Patient patient = patientRepository.save(patientMapper.toEntity(patientData));
        return patientMapper.toDTO(patient);
    }

    @Transactional
    public void deletePatient(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient with email: %s does not exist.".formatted(email), OffsetDateTime.now()));
        patientRepository.delete(patient);
    }

    @Transactional
    public PatientDTO editPatient(String email, FullPatientDataDTO patientData) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient with email %s does not exist.".formatted(email), OffsetDateTime.now()));
        validatePatientEdit(patient, patientData);
        patient.update(patientData);
        patientRepository.save(patient);
        return patientMapper.toDTO(patient);
    }

    @Transactional
    public PatientDTO editPatientPassword(String email, String password) {
        if (password == null) {
            throw new PatientIllegalArgumentException("Password cannot be set to null.", OffsetDateTime.now());
        }
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient with email %s does not exist.".formatted(email), OffsetDateTime.now()));
        patient.setPassword(password);
        patientRepository.save(patient);
        return patientMapper.toDTO(patient);
    }

    private void validateFullPatientDataDTO(FullPatientDataDTO patientData) {
        if (patientData.email() == null
                || patientData.password() == null
                || patientData.idCardNo() == null
                || patientData.firstName() == null
                || patientData.lastName() == null
                || patientData.phoneNumber() == null
                || patientData.birthday() == null) {
            throw new PatientIllegalArgumentException("There cannot be null fields in patient data.", OffsetDateTime.now());
        }
    }

    private void validateEmailAvailability(String email) {
        if (patientRepository.existsByEmail(email)) {
            throw new PatientAlreadyExistsException("Patient with email: %s already exists.".formatted(email), OffsetDateTime.now());
        }
    }

    private void validatePatientEdit(Patient patient, FullPatientDataDTO patientData) {
        validateFullPatientDataDTO(patientData);
        if (patientRepository.existsByEmail(patientData.email()) && !Objects.equals(patient.getEmail(), patientData.email())) {
            throw new PatientAlreadyExistsException("Patient with email: %s already exists.".formatted(patientData.email()), OffsetDateTime.now());
        }
        if (!patient.getIdCardNo().equals(patientData.idCardNo())) {
            throw new PatientIllegalArgumentException("ID card number cannot be changed.", OffsetDateTime.now());
        }
    }
}

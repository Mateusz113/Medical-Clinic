package com.example.demo.service;

import com.example.demo.exception.PatientIllegalArgumentException;
import com.example.demo.exception.PatientNotFoundException;
import com.example.demo.mapper.PatientMapper;
import com.example.demo.model.FullPatientDataDTO;
import com.example.demo.model.PatientDTO;
import com.example.demo.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    public List<PatientDTO> getAllPatients() {
        return patientRepository.getAllPatients()
                .stream()
                .map(PatientMapper::patientToPatientDTO)
                .collect(Collectors.toList());
    }

    public PatientDTO getPatient(String email) {
        return PatientMapper.patientToPatientDTO(patientRepository.getPatient(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient with email: %s does not exist.".formatted(email), OffsetDateTime.now())));
    }

    public PatientDTO createPatient(FullPatientDataDTO patientData) {
        if (isAnyPatientFieldNull(patientData)) {
            throw new PatientIllegalArgumentException("There cannot be null fields in patient data.", OffsetDateTime.now());
        }
        return PatientMapper.patientToPatientDTO(patientRepository.addPatient(PatientMapper.fullPatientDataDTOToPatient(patientData)));
    }

    public void deletePatient(String email) {
        if (!patientRepository.deletePatient(email)) {
            throw new PatientNotFoundException("Patient with email: %s does not exist.".formatted(email), OffsetDateTime.now());
        }
    }

    public PatientDTO editPatient(String email, FullPatientDataDTO patientData) {
        if (isAnyPatientFieldNull(patientData)) {
            throw new PatientIllegalArgumentException("There cannot be null fields in patient data.", OffsetDateTime.now());
        }
        return PatientMapper.patientToPatientDTO(patientRepository.updatePatient(email, PatientMapper.fullPatientDataDTOToPatient(patientData)));
    }

    public PatientDTO editPatientPassword(String email, String password) {
        if (password == null) {
            throw new PatientIllegalArgumentException("Password cannot be set to null.", OffsetDateTime.now());
        }
        return PatientMapper.patientToPatientDTO(patientRepository.updatePatientPassword(email, password));
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

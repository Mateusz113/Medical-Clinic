package com.example.demo.service;

import com.example.demo.exception.PatientIllegalArgumentException;
import com.example.demo.model.Patient;
import com.example.demo.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.getAllPatients();
    }

    public Patient getPatient(String email) {
        return patientRepository.getPatient(email);
    }

    public Patient createPatient(Patient patient) {
        if (isAnyPatientFieldNull(patient)) {
            throw new PatientIllegalArgumentException("There cannot be null fields in patient data.", OffsetDateTime.now());
        }
        return patientRepository.addPatient(patient);
    }

    public void deletePatient(String email) {
        patientRepository.deletePatient(email);
    }

    public Patient editPatient(String email, Patient newPatientData) {
        if (isAnyPatientFieldNull(newPatientData)) {
            throw new PatientIllegalArgumentException("There cannot be null fields in patient data.", OffsetDateTime.now());
        }
        return patientRepository.updatePatient(email, newPatientData);
    }

    public Patient editPatientPassword(String email, String password) {
        if (password == null) {
            throw new PatientIllegalArgumentException("Password cannot be set to null.", OffsetDateTime.now());
        }
        return patientRepository.updatePatientPassword(email, password);
    }

    private boolean isAnyPatientFieldNull(Patient patientData) {
        return patientData.getEmail() == null
                || patientData.getPassword() == null
                || patientData.getIdCardNo() == null
                || patientData.getFirstName() == null
                || patientData.getLastName() == null
                || patientData.getPhoneNumber() == null
                || patientData.getBirthday() == null;
    }
}

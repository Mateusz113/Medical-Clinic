package com.example.demo.service;

import com.example.demo.model.Patient;
import com.example.demo.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        return patientRepository.addPatient(patient);
    }

    public void deletePatient(String email) {
        patientRepository.deletePatient(email);
    }

    public Patient editPatient(String email, Patient newPatientData) {
        return patientRepository.updatePatient(email, newPatientData);
    }

    public Patient editPatientPassword(String email, String password) {
        return patientRepository.updatePatientPassword(email, password);
    }
}

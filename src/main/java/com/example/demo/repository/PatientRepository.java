package com.example.demo.repository;

import com.example.demo.model.Patient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PatientRepository {
    private final List<Patient> patients;

    public PatientRepository() {
        patients = new ArrayList<>();
    }

    public List<Patient> getAllPatients() {
        return patients;
    }

    public Optional<Patient> getPatient(String email) {
        return patients.stream().filter(patient -> patient.getEmail().equals(email)).findFirst();
    }

    public boolean addPatient(Patient patient) {
        return patients.add(patient);
    }

    public boolean deletePatient(String email) {
        return patients.removeIf(patient -> patient.getEmail().equals(email));
    }
}

package com.example.demo.repository;

import com.example.demo.exception.PatientAlreadyExistsException;
import com.example.demo.exception.PatientNotFoundException;
import com.example.demo.model.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PatientRepository {
    private final List<Patient> patients;

    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients);
    }

    public Patient getPatient(String email) {
        Patient existingPatient = getPatientReference(email);
        return new Patient(
                existingPatient.getEmail(),
                existingPatient.getPassword(),
                existingPatient.getIdCardNo(),
                existingPatient.getFirstName(),
                existingPatient.getLastName(),
                existingPatient.getPhoneNumber(),
                existingPatient.getBirthday()
        );
    }

    public Patient addPatient(Patient patient) {
        boolean emailIsAvailable = patients.stream()
                .noneMatch(existingPatient -> patient.getEmail().equals(existingPatient.getEmail()));
        if (!emailIsAvailable) {
            throw new PatientAlreadyExistsException("Patient with email: %s already exists".formatted(patient.getEmail()), OffsetDateTime.now());
        }
        patients.add(patient);
        return patient;
    }

    public void deletePatient(String email) {
        if (!patients.removeIf(patient -> patient.getEmail().equals(email))) {
            throw new PatientNotFoundException("Patient with email: %s does not exist".formatted(email), OffsetDateTime.now());
        }
    }

    public Patient updatePatient(String email, Patient newPatientData) {
        Patient patientToUpdate = getPatientReference(email);
        patientToUpdate.setEmail(newPatientData.getEmail());
        patientToUpdate.setPassword(newPatientData.getPassword());
        patientToUpdate.setIdCardNo(newPatientData.getIdCardNo());
        patientToUpdate.setFirstName(newPatientData.getFirstName());
        patientToUpdate.setLastName(newPatientData.getLastName());
        patientToUpdate.setPhoneNumber(newPatientData.getPhoneNumber());
        patientToUpdate.setBirthday(newPatientData.getBirthday());
        return getPatient(newPatientData.getEmail());
    }

    private Patient getPatientReference(String email) {
        return patients.stream()
                .filter(patient -> patient.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new PatientNotFoundException("Patient with email: %s does not exist".formatted(email), OffsetDateTime.now()));
    }
}

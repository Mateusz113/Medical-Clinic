package com.example.demo.repository;

import com.example.demo.exception.PatientAlreadyExistsException;
import com.example.demo.exception.PatientIllegalArgumentException;
import com.example.demo.exception.PatientNotFoundException;
import com.example.demo.model.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PatientRepository {
    private final List<Patient> patients;

    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients);
    }

    public Optional<Patient> getPatient(String email) {
        Optional<Patient> existingPatient = getPatientReference(email);
        return existingPatient.map(patient ->
                new Patient(
                        patient.getEmail(),
                        patient.getPassword(),
                        patient.getIdCardNo(),
                        patient.getFirstName(),
                        patient.getLastName(),
                        patient.getPhoneNumber(),
                        patient.getBirthday()
                )
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

    public boolean deletePatient(String email) {
        return patients.removeIf(patient -> patient.getEmail().equals(email));
    }

    public Patient updatePatient(String email, Patient newPatientData) {
        Patient patientToUpdate = getPatientReference(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient with email: %s does not exist.".formatted(email), OffsetDateTime.now()));
        if (!patientToUpdate.getIdCardNo().equals(newPatientData.getIdCardNo())) {
            throw new PatientIllegalArgumentException("ID card number cannot be changed.", OffsetDateTime.now());
        }
        boolean isNewPatientEmailAvailable = patients.stream()
                .noneMatch(patient -> patient.getEmail().equals(newPatientData.getEmail()));
        if (!isNewPatientEmailAvailable && !Objects.equals(email, newPatientData.getEmail())) {
            throw new PatientIllegalArgumentException("Email: %s is already taken.".formatted(newPatientData.getEmail()), OffsetDateTime.now());
        }
        patientToUpdate.setEmail(newPatientData.getEmail());
        patientToUpdate.setPassword(newPatientData.getPassword());
        patientToUpdate.setIdCardNo(newPatientData.getIdCardNo());
        patientToUpdate.setFirstName(newPatientData.getFirstName());
        patientToUpdate.setLastName(newPatientData.getLastName());
        patientToUpdate.setPhoneNumber(newPatientData.getPhoneNumber());
        patientToUpdate.setBirthday(newPatientData.getBirthday());
        return getPatient(newPatientData.getEmail()).get();
    }

    public Patient updatePatientPassword(String email, String password) {
        Patient patientToUpdate = getPatientReference(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient with email: %s does not exist.".formatted(email), OffsetDateTime.now()));
        patientToUpdate.setPassword(password);
        return getPatient(email).get();
    }

    private Optional<Patient> getPatientReference(String email) {
        return patients.stream()
                .filter(patient -> patient.getEmail().equals(email))
                .findFirst();
    }
}

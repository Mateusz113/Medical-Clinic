package com.example.demo.repository;

import com.example.demo.exception.PatientOperationException;
import com.example.demo.model.Patient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PatientRepository {
    private final List<Patient> patients;

    public PatientRepository() {
        patients = new ArrayList<>();
    }

    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients);
    }

    public Patient getPatient(String email) {
        return patients.stream()
                .filter(patient -> patient.getEmail().equals(email))
                .findFirst()
                .map(patient -> new Patient(
                        patient.getEmail(),
                        patient.getPassword(),
                        patient.getIdCardNo(),
                        patient.getFirstName(),
                        patient.getLastName(),
                        patient.getPhoneNumber(),
                        patient.getBirthday()
                ))
                .orElseThrow(() -> new PatientOperationException("Could not retrieve patient. Patient with given email does not exist."));
    }

    public Patient addPatient(Patient patient) {
        boolean emailIsAvailable = patients.stream()
                .noneMatch(existingPatient -> patient.getEmail().equals(existingPatient.getEmail()));
        if (!emailIsAvailable) {
            throw new PatientOperationException("Could not create a new patient. Patient with given email already exists.");
        }
        patients.add(patient);
        return patient;
    }

    public void deletePatient(String email) {
        if (!patients.removeIf(patient -> patient.getEmail().equals(email))) {
            throw new PatientOperationException("Could not delete patient. Patient with given email does not exist.");
        }
    }

    public Patient updatePatient(String email, Patient newPatientData) {
        Patient patientToUpdate = patients.stream()
                .filter(patient -> patient.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new PatientOperationException("Could not edit patient. Patient with given email does not exist."));
        patientToUpdate.setEmail(newPatientData.getEmail());
        patientToUpdate.setPassword(newPatientData.getPassword());
        patientToUpdate.setIdCardNo(newPatientData.getIdCardNo());
        patientToUpdate.setFirstName(newPatientData.getFirstName());
        patientToUpdate.setLastName(newPatientData.getLastName());
        patientToUpdate.setPhoneNumber(newPatientData.getPhoneNumber());
        patientToUpdate.setBirthday(newPatientData.getBirthday());
        return getPatient(email);
    }
}

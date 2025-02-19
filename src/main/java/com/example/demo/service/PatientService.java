package com.example.demo.service;

import com.example.demo.exception.PatientCreationException;
import com.example.demo.exception.PatientDeletionException;
import com.example.demo.exception.PatientEditionException;
import com.example.demo.model.Patient;
import com.example.demo.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.getAllPatients();
    }

    public Patient getPatient(String email) {
        Optional<Patient> patientOptional = patientRepository.getPatient(email);
        return patientOptional.orElseThrow();
    }

    public Patient createPatient(Patient patient) {
        if (patientRepository.addPatient(patient)) {
            return patient;
        } else {
            throw new PatientCreationException("Failed to create new patient.");
        }
    }

    public void deletePatient(String email) {
        if (!patientRepository.deletePatient(email)) {
            throw new PatientDeletionException("Failed to delete patient with given email.");
        }
    }

    public Patient editPatient(String email, Patient newPatientInfo) {
        Optional<Patient> patientToEdit = patientRepository.getPatient(email);
        patientToEdit.ifPresentOrElse(
                patient -> updatePatientInfo(patient, newPatientInfo),
                () -> {
                    throw new PatientEditionException("Failed to edit patient information.");
                }
        );
        return newPatientInfo;
    }

    private void updatePatientInfo(Patient patientToUpdate, Patient newPatientInfo) {
        patientToUpdate.setEmail(newPatientInfo.getEmail());
        patientToUpdate.setPassword(newPatientInfo.getPassword());
        patientToUpdate.setIdCardNo(newPatientInfo.getIdCardNo());
        patientToUpdate.setFirstName(newPatientInfo.getFirstName());
        patientToUpdate.setLastName(newPatientInfo.getLastName());
        patientToUpdate.setPhoneNumber(newPatientInfo.getPhoneNumber());
        patientToUpdate.setBirthday(newPatientInfo.getBirthday());
    }
}

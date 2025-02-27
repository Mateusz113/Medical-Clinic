package com.example.demo.mapper;

import com.example.demo.model.FullPatientDataDTO;
import com.example.demo.model.Patient;
import com.example.demo.model.PatientDTO;

public class PatientMapper {
    public static PatientDTO patientToPatientDTO(Patient patient) {
        return PatientDTO.builder()
                .email(patient.getEmail())
                .idCardNo(patient.getIdCardNo())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .phoneNumber(patient.getPhoneNumber())
                .birthday(patient.getBirthday())
                .build();
    }

    public static Patient fullPatientDataDTOToPatient(FullPatientDataDTO patientData) {
        return Patient.builder()
                .email(patientData.email())
                .password(patientData.password())
                .idCardNo(patientData.idCardNo())
                .firstName(patientData.firstName())
                .lastName(patientData.lastName())
                .phoneNumber(patientData.phoneNumber())
                .birthday(patientData.birthday())
                .build();
    }
}
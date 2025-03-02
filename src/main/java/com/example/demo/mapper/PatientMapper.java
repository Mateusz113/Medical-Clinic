package com.example.demo.mapper;

import com.example.demo.model.FullPatientDataDTO;
import com.example.demo.model.Patient;
import com.example.demo.model.PatientDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    PatientDTO toDTO(Patient patient);

    Patient toEntity(FullPatientDataDTO patientData);
}
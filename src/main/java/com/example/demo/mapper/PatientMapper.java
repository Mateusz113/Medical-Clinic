package com.example.demo.mapper;

import com.example.demo.model.patient.FullPatientDataDTO;
import com.example.demo.model.patient.Patient;
import com.example.demo.model.patient.PatientDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    PatientDTO toDTO(Patient patient);

    @Mapping(target = "id", expression = "java(null)")
    Patient toEntity(FullPatientDataDTO patientData);
}
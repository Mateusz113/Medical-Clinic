package com.example.demo.mapper;

import com.example.demo.model.FullPatientDataDTO;
import com.example.demo.model.Patient;
import com.example.demo.model.PatientDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    PatientDTO toDTO(Patient patient);

    @Mapping(target = "id", expression = "java(null)")
    Patient toEntity(FullPatientDataDTO patientData);
}
package com.example.demo.mapper;

import com.example.demo.command.patient.UpsertPatientCommand;
import com.example.demo.model.patient.Patient;
import com.example.demo.model.patient.PatientDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    PatientDTO toDTO(Patient patient);

    @Mapping(target = "id", ignore = true)
    Patient toEntity(UpsertPatientCommand upsertPatientCommand);
}
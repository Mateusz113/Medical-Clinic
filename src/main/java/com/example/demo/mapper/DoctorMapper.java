package com.example.demo.mapper;

import com.example.demo.command.doctor.UpsertDoctorCommand;
import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.doctor.DoctorDTO;
import com.example.demo.model.doctor.SimpleDoctorDTO;
import com.example.demo.model.facility.Facility;
import com.example.demo.model.facility.SimpleFacilityDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    DoctorDTO toDTO(Doctor doctor);

    SimpleDoctorDTO toSimpleDTO(Doctor doctor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "facilities", ignore = true)
    @Mapping(target = "visits", ignore = true)
    Doctor toEntity(UpsertDoctorCommand upsertDoctorCommand);

    Set<Doctor> toEntities(Set<UpsertDoctorCommand> upsertDoctorCommandSet);

    default SimpleFacilityDTO getSimpleFacilityDTO(Facility facility) {
        return Mappers.getMapper(FacilityMapper.class).toSimpleDTO(facility);
    }
}

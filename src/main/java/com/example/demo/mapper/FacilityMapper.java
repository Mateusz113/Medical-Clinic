package com.example.demo.mapper;

import com.example.demo.command.doctor.UpsertDoctorCommand;
import com.example.demo.command.facility.InsertFacilityCommand;
import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.facility.Facility;
import com.example.demo.model.facility.FacilityDTO;
import com.example.demo.model.facility.SimpleFacilityDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FacilityMapper {
    FacilityDTO toDTO(Facility facility);

    List<FacilityDTO> toDTOs(List<Facility> facilities);

    SimpleFacilityDTO toSimpleDTO(Facility facility);

    @Mapping(target = "id", ignore = true)
    Facility toEntity(InsertFacilityCommand insertFacilityCommand);

    default Doctor getDoctor(UpsertDoctorCommand upsertDoctorCommand) {
        return Mappers.getMapper(DoctorMapper.class).toEntity(upsertDoctorCommand);
    }
}

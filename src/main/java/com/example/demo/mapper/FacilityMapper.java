package com.example.demo.mapper;

import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.doctor.FullDoctorDataDTO;
import com.example.demo.model.doctor.SimpleDoctorDTO;
import com.example.demo.model.facility.Facility;
import com.example.demo.model.facility.FacilityDTO;
import com.example.demo.model.facility.FullFacilityDataDTO;
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

    @Mapping(target = "id", expression = "java(null)")
    Facility toEntity(FullFacilityDataDTO facilityData);

    default SimpleDoctorDTO getSimpleDoctorDTO(Doctor doctor) {
        return Mappers.getMapper(DoctorMapper.class).toSimpleDTO(doctor);
    }

    default Doctor getDoctor(FullDoctorDataDTO doctorData) {
        return Mappers.getMapper(DoctorMapper.class).toEntity(doctorData);
    }
}

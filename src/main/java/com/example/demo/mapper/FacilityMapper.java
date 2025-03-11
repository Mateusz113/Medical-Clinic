package com.example.demo.mapper;

import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.doctor.SimpleDoctorDTO;
import com.example.demo.model.facility.Facility;
import com.example.demo.model.facility.FacilityDTO;
import com.example.demo.model.facility.FullFacilityDataDTO;
import com.example.demo.model.facility.SimpleFacilityDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FacilityMapper {
    FacilityDTO toDTO(Facility facility);

    SimpleFacilityDTO toSimpleDTO(Facility facility);

    @Mapping(target = "id", expression = "java(null)")
    Facility toEntity(FullFacilityDataDTO facilityData);

    default SimpleDoctorDTO getSimpleDoctorDTO(Doctor doctor) {
        return Mappers.getMapper(DoctorMapper.class).toSimpleDTO(doctor);
    }
}

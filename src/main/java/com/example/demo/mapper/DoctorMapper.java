package com.example.demo.mapper;

import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.doctor.DoctorDTO;
import com.example.demo.model.doctor.FullDoctorDataDTO;
import com.example.demo.model.doctor.SimpleDoctorDTO;
import com.example.demo.model.facility.Facility;
import com.example.demo.model.facility.SimpleFacilityDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    DoctorDTO toDTO(Doctor doctor);

    SimpleDoctorDTO toSimpleDTO(Doctor doctor);

    @Mapping(target = "id", expression = "java(null)")
    Doctor toEntity(FullDoctorDataDTO doctorData);

    Set<Doctor> toEntities(Set<FullDoctorDataDTO> doctorDataDTOS);

    default SimpleFacilityDTO getSimpleFacilityDTO(Facility facility) {
        return Mappers.getMapper(FacilityMapper.class).toSimpleDTO(facility);
    }
}

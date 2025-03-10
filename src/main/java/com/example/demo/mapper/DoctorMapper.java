package com.example.demo.mapper;

import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.doctor.DoctorDTO;
import com.example.demo.model.doctor.FullDoctorDataDTO;
import com.example.demo.model.facility.Facility;
import com.example.demo.model.facility.FacilityDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    DoctorDTO toDTO(Doctor doctor);

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "facilities", expression = "java(new java.util.HashSet<>())")
    Doctor toEntity(FullDoctorDataDTO doctorData);

    @Mapping(target = "doctors", ignore = true)
    FacilityDTO getFacilityDto(Facility facility);
}

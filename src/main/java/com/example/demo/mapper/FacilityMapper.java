package com.example.demo.mapper;

import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.doctor.DoctorDTO;
import com.example.demo.model.facility.Facility;
import com.example.demo.model.facility.FacilityDTO;
import com.example.demo.model.facility.FullFacilityDataDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FacilityMapper {
    @Mapping(target = "doctors.facilities", ignore = true)
    FacilityDTO toDTO(Facility facility);

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "doctors", expression = "java(new java.util.HashSet<>())")
    Facility toEntity(FullFacilityDataDTO facilityData);

    @Mapping(target = "facilities", ignore = true)
    DoctorDTO getDoctorDTO(Doctor doctor);
}

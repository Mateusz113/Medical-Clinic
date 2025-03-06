package com.example.demo.mapper;

import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.doctor.DoctorDataDTO;
import com.example.demo.model.facility.Facility;
import com.example.demo.model.facility.FacilityDTO;
import com.example.demo.model.facility.FacilityDataDTO;
import com.example.demo.model.facility.FullFacilityDataDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FacilityMapper {
    FacilityDTO toDTO(Facility facility);

    FacilityDataDTO toDataDTO(Facility facility);

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "doctors", expression = "java(new java.util.HashSet<>())")
    Facility toEntity(FullFacilityDataDTO facilityData);

    default DoctorDataDTO doctorToDoctorDataDTO(Doctor doctor) {
        return Mappers.getMapper(DoctorMapper.class).toDataDTO(doctor);
    }
}

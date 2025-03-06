package com.example.demo.mapper;

import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.doctor.DoctorDTO;
import com.example.demo.model.doctor.DoctorDataDTO;
import com.example.demo.model.doctor.FullDoctorDataDTO;
import com.example.demo.model.facility.Facility;
import com.example.demo.model.facility.FacilityDataDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    DoctorDTO toDTO(Doctor doctor);

    DoctorDataDTO toDataDTO(Doctor doctor);

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "facilities", expression = "java(new java.util.HashSet<>())")
    Doctor toEntity(FullDoctorDataDTO doctorData);

    default FacilityDataDTO facilityToFacilityDataDTO(Facility facility) {
        return Mappers.getMapper(FacilityMapper.class).toDataDTO(facility);
    }
}

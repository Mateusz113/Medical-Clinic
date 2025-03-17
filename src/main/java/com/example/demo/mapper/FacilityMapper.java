package com.example.demo.mapper;

import com.example.demo.model.facility.Facility;
import com.example.demo.model.facility.FacilityDTO;
import com.example.demo.model.facility.FullFacilityDataDTO;
import com.example.demo.model.facility.SimpleFacilityDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = DoctorMapper.class)
public interface FacilityMapper {
    FacilityDTO toDTO(Facility facility);

    List<FacilityDTO> toDTOs(List<Facility> facilities);

    SimpleFacilityDTO toSimpleDTO(Facility facility);

    @Mapping(target = "id", expression = "java(null)")
    Facility toEntity(FullFacilityDataDTO facilityData);
}

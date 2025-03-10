package com.example.demo.model.facility;

import com.example.demo.json_view.DoctorJsonViews;
import com.example.demo.json_view.FacilityJsonViews;
import com.example.demo.model.doctor.DoctorDTO;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Set;

public record FacilityDTO(
        @JsonView({DoctorJsonViews.Partial.class, FacilityJsonViews.Partial.class})
        Long id,
        @JsonView({DoctorJsonViews.Partial.class, FacilityJsonViews.Partial.class})
        String name,
        @JsonView({DoctorJsonViews.Partial.class, FacilityJsonViews.Partial.class})
        String city,
        @JsonView({DoctorJsonViews.Partial.class, FacilityJsonViews.Partial.class})
        String zipCode,
        @JsonView({DoctorJsonViews.Partial.class, FacilityJsonViews.Partial.class})
        String street,
        @JsonView({DoctorJsonViews.Partial.class, FacilityJsonViews.Partial.class})
        String buildingNumber,
        @JsonView({DoctorJsonViews.Standalone.class, FacilityJsonViews.Partial.class})
        Set<DoctorDTO> doctors
) {
}

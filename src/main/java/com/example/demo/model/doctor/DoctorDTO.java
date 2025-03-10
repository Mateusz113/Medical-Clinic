package com.example.demo.model.doctor;

import com.example.demo.json_view.DoctorJsonViews;
import com.example.demo.json_view.FacilityJsonViews;
import com.example.demo.model.facility.FacilityDTO;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Set;

public record DoctorDTO(
        @JsonView({DoctorJsonViews.Partial.class, FacilityJsonViews.Partial.class})
        Long id,
        @JsonView({DoctorJsonViews.Partial.class, FacilityJsonViews.Partial.class})
        String email,
        @JsonView({DoctorJsonViews.Partial.class, FacilityJsonViews.Partial.class})
        String firstName,
        @JsonView({DoctorJsonViews.Partial.class, FacilityJsonViews.Partial.class})
        String lastName,
        @JsonView({DoctorJsonViews.Partial.class, FacilityJsonViews.Partial.class})
        String specialization,
        @JsonView({DoctorJsonViews.Partial.class, FacilityJsonViews.Standalone.class})
        Set<FacilityDTO> facilities
) {
}

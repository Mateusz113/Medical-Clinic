package com.example.demo.argument_matcher;

import com.example.demo.model.facility.Facility;
import lombok.RequiredArgsConstructor;
import org.mockito.ArgumentMatcher;

import java.util.Objects;

@RequiredArgsConstructor
public class FacilityArgumentMatcher implements ArgumentMatcher<Facility> {
    private final Facility left;

    @Override
    public boolean matches(Facility right) {
        return Objects.equals(left.getId(), right.getId())
                && Objects.equals(left.getName(), right.getName())
                && Objects.equals(left.getCity(), right.getCity())
                && Objects.equals(left.getZipCode(), right.getZipCode())
                && Objects.equals(left.getStreet(), right.getStreet())
                && Objects.equals(left.getBuildingNumber(), right.getBuildingNumber())
                && Objects.equals(left.getDoctors(), right.getDoctors());
    }
}

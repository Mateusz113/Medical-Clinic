package com.example.demo.argument_matcher;

import com.example.demo.model.doctor.Doctor;
import lombok.RequiredArgsConstructor;
import org.mockito.ArgumentMatcher;

import java.util.Objects;

@RequiredArgsConstructor
public class DoctorArgumentMatcher implements ArgumentMatcher<Doctor> {
    private final Doctor left;

    @Override
    public boolean matches(Doctor right) {
        return Objects.equals(left.getId(), right.getId())
                && Objects.equals(left.getEmail(), right.getEmail())
                && Objects.equals(left.getPassword(), right.getPassword())
                && Objects.equals(left.getFirstName(), right.getFirstName())
                && Objects.equals(left.getLastName(), right.getLastName())
                && Objects.equals(left.getSpecialization(), right.getSpecialization())
                && Objects.equals(left.getFacilities(), right.getFacilities())
                && Objects.equals(left.getVisits(), right.getVisits());
    }
}

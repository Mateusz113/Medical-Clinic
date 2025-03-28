package com.example.demo.argument_matcher;

import com.example.demo.model.patient.Patient;
import lombok.RequiredArgsConstructor;
import org.mockito.ArgumentMatcher;

import java.util.Objects;

@RequiredArgsConstructor
public class PatientArgumentMatcher implements ArgumentMatcher<Patient> {
    private final Patient left;

    @Override
    public boolean matches(Patient right) {
        return Objects.equals(left.getId(), right.getId())
                && Objects.equals(left.getEmail(), right.getEmail())
                && Objects.equals(left.getPassword(), right.getPassword())
                && Objects.equals(left.getIdCardNo(), right.getIdCardNo())
                && Objects.equals(left.getFirstName(), right.getFirstName())
                && Objects.equals(left.getLastName(), right.getLastName())
                && Objects.equals(left.getPhoneNumber(), right.getPhoneNumber())
                && Objects.equals(left.getBirthday(), right.getBirthday());
    }
}

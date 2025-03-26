package com.example.demo.argument_matcher;

import com.example.demo.model.visit.Visit;
import lombok.RequiredArgsConstructor;
import org.mockito.ArgumentMatcher;

import java.util.Objects;

@RequiredArgsConstructor
public class VisitArgumentMatcher implements ArgumentMatcher<Visit> {
    private final Visit left;

    @Override
    public boolean matches(Visit right) {
        return Objects.equals(left.getId(), right.getId())
                && Objects.equals(left.getStartTime(), right.getStartTime())
                && Objects.equals(left.getEndTime(), right.getEndTime())
                && Objects.equals(left.getPatient(), right.getPatient())
                && Objects.equals(left.getDoctor(), right.getDoctor());
    }
}

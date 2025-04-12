package com.example.demo.specification;

import com.example.demo.filter.visit.VisitFilter;
import com.example.demo.model.visit.Visit;
import org.springframework.data.jpa.domain.Specification;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;

public class VisitSpecification {
    public static Specification<Visit> constructVisitSpecification(VisitFilter visitFilter, Clock clock) {
        Specification<Visit> specification = Specification.where(null);
        if (!Objects.isNull(visitFilter.visitId())) {
            specification = specification.and(visitIdEquals(visitFilter.visitId()));
        }
        if (!Objects.isNull(visitFilter.doctorId())) {
            specification = specification.and(doctorIdEquals(visitFilter.doctorId()));
        }
        if (!Objects.isNull(visitFilter.doctorSpecialization())) {
            specification = specification.and(doctorSpecializationEquals(visitFilter.doctorSpecialization()));
        }
        if (!Objects.isNull(visitFilter.patientId())) {
            specification = specification.and(patientIdEquals(visitFilter.patientId()));
        }
        if (!Objects.isNull(visitFilter.onlyAvailable()) && visitFilter.onlyAvailable()) {
            specification = specification.and(visitIsAvailable(OffsetDateTime.now(clock)));
        }
        if (!Objects.isNull(visitFilter.startTime())) {
            specification = specification.and(startTimeAfterOrEquals(visitFilter.startTime()));
        }
        if (!Objects.isNull(visitFilter.endTime())) {
            specification = specification.and(endTimeBeforeOrEquals(visitFilter.endTime()));
        }
        return specification;
    }

    private static Specification<Visit> visitIdEquals(Long visitId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), visitId);
    }

    private static Specification<Visit> doctorIdEquals(Long doctorId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("doctor").get("id"), doctorId);
    }

    private static Specification<Visit> doctorSpecializationEquals(String specialization) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("doctor").get("specialization"), specialization);
    }

    private static Specification<Visit> patientIdEquals(Long patientId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("patient").get("id"), patientId);
    }

    private static Specification<Visit> visitIsAvailable(OffsetDateTime currentTime) {
        Specification<Visit> patientIsNull = (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get("patient"));
        Specification<Visit> visitIsInTheFuture = (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("startTime"), currentTime);
        return Specification
                .where(patientIsNull)
                .and(visitIsInTheFuture);
    }

    private static Specification<Visit> startTimeAfterOrEquals(OffsetDateTime startTime) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("startTime"), startTime);
    }

    private static Specification<Visit> endTimeBeforeOrEquals(OffsetDateTime endTime) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("endTime"), endTime);
    }
}

package com.example.demo.validator;

import com.example.demo.exception.doctor.DoctorNotFoundException;
import com.example.demo.exception.visit.VisitIllegalDataException;
import com.example.demo.exception.visit.VisitNotAvailableException;
import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.visit.Visit;
import com.example.demo.model.visit.VisitCreationDTO;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.VisitRepository;

import java.time.OffsetDateTime;
import java.util.Objects;

public class VisitValidator {
    public static void validateVisitData(VisitCreationDTO visitData, VisitRepository visitRepository, DoctorRepository doctorRepository) {
        validateVisitData(visitData);
        Doctor doctor = validateExistenceAndRetrieveDoctor(visitData.doctorId(), doctorRepository);
        validateVisitDates(visitData.startTime(), visitData.endTime(), visitRepository, doctor);
    }

    public static void validateVisitAvailability(Visit visit) {
        if (visit.getStartTime().isBefore(OffsetDateTime.now())) {
            throw new VisitNotAvailableException("Patient cannot register to the past visits.", OffsetDateTime.now());
        }
        if (visit.getPatient() != null) {
            throw new VisitNotAvailableException("Patient is already registered to that visit.", OffsetDateTime.now());
        }
    }

    private static void validateVisitDates(OffsetDateTime startTime, OffsetDateTime endTime, VisitRepository visitRepository, Doctor doctor) {
        if (startTime.isBefore(OffsetDateTime.now())) {
            throw new VisitIllegalDataException("The visit cannot be set in the past.", OffsetDateTime.now());
        }
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new VisitIllegalDataException("The visit end date has to be later than start date.", OffsetDateTime.now());
        }
        if (startTime.getMinute() % 15 != 0 || endTime.getMinute() % 15 != 0) {
            throw new VisitIllegalDataException("The visit time must be set to a full quarter-hour increment e.g. 13:15.", OffsetDateTime.now());
        }
        if (visitRepository.existsBetweenDatesInclusive(startTime, endTime, doctor)) {
            throw new VisitIllegalDataException("There is a visit already scheduled at that time.", OffsetDateTime.now());
        }
    }

    private static Doctor validateExistenceAndRetrieveDoctor(Long doctorId, DoctorRepository doctorRepository) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with id: %d does not exist.".formatted(doctorId), OffsetDateTime.now()));
    }

    private static void validateVisitData(VisitCreationDTO visitData) {
        if (Objects.isNull(visitData.doctorId())
                || Objects.isNull(visitData.startTime())
                || Objects.isNull(visitData.endTime())) {
            throw new VisitIllegalDataException("There cannot be nulls in visit data.", OffsetDateTime.now());
        }
    }
}

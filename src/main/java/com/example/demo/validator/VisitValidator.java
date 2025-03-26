package com.example.demo.validator;

import com.example.demo.command.visit.InsertVisitCommand;
import com.example.demo.exception.doctor.DoctorNotFoundException;
import com.example.demo.exception.visit.VisitIllegalDataException;
import com.example.demo.exception.visit.VisitNotAvailableException;
import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.visit.Visit;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.VisitRepository;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;

public class VisitValidator {
    public static void validateVisitData(InsertVisitCommand insertVisitCommand, VisitRepository visitRepository, DoctorRepository doctorRepository, Clock clock) {
        validateVisitData(insertVisitCommand, clock);
        Doctor doctor = validateExistenceAndRetrieveDoctor(insertVisitCommand.doctorId(), doctorRepository, clock);
        validateVisitDates(insertVisitCommand.startTime(), insertVisitCommand.endTime(), visitRepository, doctor, clock);
    }

    public static void validateVisitAvailability(Visit visit, Clock clock) {
        if (visit.getStartTime().isBefore(OffsetDateTime.now(clock))) {
            throw new VisitNotAvailableException("Patient cannot register to the past visits.", OffsetDateTime.now(clock));
        }
        if (visit.getPatient() != null) {
            throw new VisitNotAvailableException("Patient is already registered to that visit.", OffsetDateTime.now(clock));
        }
    }

    private static void validateVisitDates(OffsetDateTime startTime, OffsetDateTime endTime, VisitRepository visitRepository, Doctor doctor, Clock clock) {
        if (startTime.isBefore(OffsetDateTime.now(clock))) {
            throw new VisitIllegalDataException("The visit cannot be set in the past.", OffsetDateTime.now(clock));
        }
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new VisitIllegalDataException("The visit end date has to be later than start date.", OffsetDateTime.now(clock));
        }
        if (startTime.getMinute() % 15 != 0 || endTime.getMinute() % 15 != 0) {
            throw new VisitIllegalDataException("The visit time must be set to a full quarter-hour increment e.g. 13:15.", OffsetDateTime.now(clock));
        }
        if (visitRepository.existsBetweenDatesInclusive(startTime, endTime, doctor)) {
            throw new VisitIllegalDataException("There is a visit already scheduled at that time.", OffsetDateTime.now(clock));
        }
    }

    private static Doctor validateExistenceAndRetrieveDoctor(Long doctorId, DoctorRepository doctorRepository, Clock clock) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with id: %d does not exist.".formatted(doctorId), OffsetDateTime.now(clock)));
    }

    private static void validateVisitData(InsertVisitCommand insertVisitCommand, Clock clock) {
        if (Objects.isNull(insertVisitCommand.doctorId())
                || Objects.isNull(insertVisitCommand.startTime())
                || Objects.isNull(insertVisitCommand.endTime())) {
            throw new VisitIllegalDataException("There cannot be nulls in visit data.", OffsetDateTime.now(clock));
        }
    }
}

package com.example.demo.validator;

import com.example.demo.exception.doctor.DoctorAlreadyExistsException;
import com.example.demo.exception.doctor.DoctorIllegalDataException;
import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.doctor.FullDoctorDataDTO;
import com.example.demo.repository.DoctorRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

public class DoctorValidator {
    public static void validateDoctorCreation(FullDoctorDataDTO doctorData, DoctorRepository doctorRepository) {
        validateDoctorData(doctorData);
        validateEmailAvailability(doctorData.email(), doctorRepository);
    }

    public static void validateDoctorBulkCreation(List<FullDoctorDataDTO> doctorData) {
        doctorData.forEach(DoctorValidator::validateDoctorData);
    }

    public static void validateDoctorEdit(Doctor doctor, FullDoctorDataDTO doctorData, DoctorRepository doctorRepository) {
        validateDoctorData(doctorData);
        if (doctorRepository.existsByEmail(doctorData.email()) && !Objects.equals(doctorData.email(), doctor.getEmail())) {
            throw new DoctorAlreadyExistsException("Doctor with email: %s already exists.".formatted(doctorData.email()), OffsetDateTime.now());
        }
    }

    private static void validateDoctorData(FullDoctorDataDTO doctorData) {
        if (Objects.isNull(doctorData.email())
                || Objects.isNull(doctorData.password())
                || Objects.isNull(doctorData.firstName())
                || Objects.isNull(doctorData.lastName())
                || Objects.isNull(doctorData.specialization())) {
            throw new DoctorIllegalDataException("There cannot be null fields in doctor.", OffsetDateTime.now());
        }
    }

    private static void validateEmailAvailability(String email, DoctorRepository doctorRepository) {
        if (doctorRepository.existsByEmail(email)) {
            throw new DoctorAlreadyExistsException("Doctor with email: %s already exists.".formatted(email), OffsetDateTime.now());
        }
    }
}

package com.example.demo.validator;

import com.example.demo.exception.doctor.DoctorAlreadyExistsException;
import com.example.demo.exception.doctor.DoctorIllegalDataException;
import com.example.demo.exception.patient.PatientAlreadyExistsException;
import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.doctor.FullDoctorDataDTO;
import com.example.demo.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DoctorValidator {
    private final DoctorRepository doctorRepository;

    public void validateDoctorCreation(FullDoctorDataDTO doctorData) {
        validateDoctorData(doctorData);
        validateEmailAvailability(doctorData.email());
    }

    public void validateDoctorBulkCreation(List<FullDoctorDataDTO> doctorData) {
        doctorData.forEach(this::validateDoctorData);
    }

    public void validateDoctorEdit(Doctor doctor, FullDoctorDataDTO doctorData) {
        validateDoctorData(doctorData);
        if (doctorRepository.existsByEmail(doctorData.email()) && !Objects.equals(doctorData.email(), doctor.getEmail())) {
            throw new PatientAlreadyExistsException("Doctor with email: %s already exists.".formatted(doctorData.email()), OffsetDateTime.now());
        }
    }

    private void validateDoctorData(FullDoctorDataDTO doctorData) {
        if (Objects.isNull(doctorData.email())
                || Objects.isNull(doctorData.password())
                || Objects.isNull(doctorData.firstName())
                || Objects.isNull(doctorData.lastName())
                || Objects.isNull(doctorData.specialization())) {
            throw new DoctorIllegalDataException("There cannot be null fields in doctor.", OffsetDateTime.now());
        }
    }

    private void validateEmailAvailability(String email) {
        if (doctorRepository.existsByEmail(email)) {
            throw new DoctorAlreadyExistsException("Doctor with email: %s already exists.".formatted(email), OffsetDateTime.now());
        }
    }
}

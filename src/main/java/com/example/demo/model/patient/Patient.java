package com.example.demo.model.patient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;

@Entity(name = "PATIENTS")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(unique = true)
    private String email;
    private String password;
    private String idCardNo;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthday;

    public void update(FullPatientDataDTO patientData) {
        this.email = patientData.email();
        this.password = patientData.password();
        this.idCardNo = patientData.idCardNo();
        this.firstName = patientData.firstName();
        this.lastName = patientData.lastName();
        this.phoneNumber = patientData.phoneNumber();
        this.birthday = patientData.birthday();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient patient)) return false;
        return Objects.equals(getEmail(), patient.getEmail());
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(getEmail());
    }
}

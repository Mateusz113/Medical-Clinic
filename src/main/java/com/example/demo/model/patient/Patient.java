package com.example.demo.model.patient;

import com.example.demo.command.patient.UpsertPatientCommand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;

@Entity(name = "PATIENTS")
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
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

    public void update(UpsertPatientCommand upsertPatientCommand) {
        this.email = upsertPatientCommand.email();
        this.password = upsertPatientCommand.password();
        this.idCardNo = upsertPatientCommand.idCardNo();
        this.firstName = upsertPatientCommand.firstName();
        this.lastName = upsertPatientCommand.lastName();
        this.phoneNumber = upsertPatientCommand.phoneNumber();
        this.birthday = upsertPatientCommand.birthday();
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

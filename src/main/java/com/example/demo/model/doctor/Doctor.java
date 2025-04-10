package com.example.demo.model.doctor;

import com.example.demo.command.doctor.UpsertDoctorCommand;
import com.example.demo.model.facility.Facility;
import com.example.demo.model.visit.Visit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "DOCTORS")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(unique = true)
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String specialization;
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "FACILITIES_DOCTORS",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    private Set<Facility> facilities = new HashSet<>();
    @Builder.Default
    @OneToMany(mappedBy = "doctor")
    private Set<Visit> visits = new HashSet<>();

    public void update(UpsertDoctorCommand upsertDoctorCommand) {
        this.email = upsertDoctorCommand.email();
        this.password = upsertDoctorCommand.password();
        this.firstName = upsertDoctorCommand.firstName();
        this.lastName = upsertDoctorCommand.lastName();
        this.specialization = upsertDoctorCommand.specialization();
    }

    public void addFacility(Facility facility) {
        facilities.add(facility);
    }

    public void removeFacility(Facility facility) {
        facilities.remove(facility);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doctor doctor)) return false;
        return Objects.equals(getEmail(), doctor.getEmail());
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(getEmail());
    }
}

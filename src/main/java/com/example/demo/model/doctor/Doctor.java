package com.example.demo.model.doctor;

import com.example.demo.model.facility.Facility;
import com.example.demo.model.visit.Visit;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "DOCTORS")
@NoArgsConstructor
@Getter
@Setter
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;
    @Column(unique = true)
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String specialization;
    @ManyToMany
    @JoinTable(
            name = "FACILITIES_DOCTORS",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    private Set<Facility> facilities = new HashSet<>();
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.REMOVE)
    private Set<Visit> visits = new HashSet<>();

    public void update(FullDoctorDataDTO doctorData) {
        this.email = doctorData.email();
        this.password = doctorData.password();
        this.firstName = doctorData.firstName();
        this.lastName = doctorData.lastName();
        this.specialization = doctorData.specialization();
    }

    public void addFacility(Facility facility) {
        facilities.add(facility);
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

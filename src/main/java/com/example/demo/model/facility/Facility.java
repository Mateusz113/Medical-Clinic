package com.example.demo.model.facility;

import com.example.demo.command.facility.UpsertFacilityCommand;
import com.example.demo.model.doctor.Doctor;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "FACILITIES")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(unique = true)
    private String name;
    private String city;
    private String zipCode;
    private String street;
    private String buildingNumber;
    @ManyToMany(mappedBy = "facilities", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Doctor> doctors = new HashSet<>();

    public void update(UpsertFacilityCommand upsertFacilityCommand) {
        this.name = upsertFacilityCommand.name();
        this.city = upsertFacilityCommand.city();
        this.zipCode = upsertFacilityCommand.zipCode();
        this.street = upsertFacilityCommand.street();
        this.buildingNumber = upsertFacilityCommand.buildingNumber();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Facility facility)) return false;
        return Objects.equals(getName(), facility.getName());
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(getName());
    }
}

package com.example.demo.model.facility;

import com.example.demo.model.doctor.Doctor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Entity(name = "FACILITIES")
@NoArgsConstructor
@Getter
@Setter
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
    @ManyToMany(mappedBy = "facilities")
    private Set<Doctor> doctors;

    public void update(FullFacilityDataDTO facilityData) {
        this.name = facilityData.name();
        this.city = facilityData.city();
        this.zipCode = facilityData.zipCode();
        this.street = facilityData.street();
        this.buildingNumber = facilityData.buildingNumber();
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

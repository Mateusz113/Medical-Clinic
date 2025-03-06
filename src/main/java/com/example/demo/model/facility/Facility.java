package com.example.demo.model.facility;

import com.example.demo.model.doctor.Doctor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

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
    @ManyToMany
    @JoinTable(
            name = "FACILITIES_DOCTORS",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    private Set<Doctor> doctors;

    public void update(FullFacilityDataDTO facilityData) {
        this.name = facilityData.name();
        this.city = facilityData.city();
        this.zipCode = facilityData.zipCode();
        this.street = facilityData.street();
        this.buildingNumber = facilityData.buildingNumber();
    }

    public boolean addDoctor(Doctor doctor) {
        return doctors.add(doctor);
    }

    public boolean removeDoctor(Doctor doctor) {
        return doctors.remove(doctor);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Facility facility = (Facility) o;
        return getId() != null && Objects.equals(getId(), facility.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

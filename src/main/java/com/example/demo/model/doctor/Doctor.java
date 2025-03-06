package com.example.demo.model.doctor;

import com.example.demo.model.facility.Facility;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

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
    @ManyToMany(mappedBy = "doctors")
    private Set<Facility> facilities;

    public void update(FullDoctorDataDTO doctorData) {
        this.email = doctorData.email();
        this.password = doctorData.password();
        this.firstName = doctorData.firstName();
        this.lastName = doctorData.lastName();
        this.specialization = doctorData.specialization();
    }

    public boolean addFacility(Facility facility) {
        return facilities.add(facility);
    }

    public boolean removeFacility(Facility facility) {
        return facilities.remove(facility);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Doctor doctor = (Doctor) o;
        return getId() != null && Objects.equals(getId(), doctor.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

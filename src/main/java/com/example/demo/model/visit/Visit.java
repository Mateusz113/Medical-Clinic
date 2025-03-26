package com.example.demo.model.visit;

import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.patient.Patient;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Entity(name = "VISITS")
@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    @ManyToOne
    @JoinColumn
    private Patient patient;
    @ManyToOne
    @JoinColumn
    private Doctor doctor;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Visit visit)) {
            return false;
        }
        return id != null && id.equals(visit.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

package com.example.demo.repository;

import com.example.demo.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByEmail(String email);

    int deleteByEmail(String email);

    @Modifying
    @Query("""
            update PATIENTS p
            set
            p.email = :#{#patientData.email},
            p.password = :#{#patientData.password},
            p.firstName = :#{#patientData.firstName},
            p.lastName = :#{#patientData.lastName},
            p.phoneNumber = :#{#patientData.phoneNumber},
            p.birthday = :#{#patientData.birthday}
            where lower(p.email) = lower(:email)
            """)
    int update(@Param("email") String email, @Param("patientData") Patient patientData);

    @Modifying
    @Query("update PATIENTS p set p.password = :password where lower(p.email) = lower(:email) ")
    int updatePassword(@Param("email") String email, @Param("password") String password);

    boolean existsByEmail(String email);
}

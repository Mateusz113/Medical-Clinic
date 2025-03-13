package com.example.demo.repository;

import com.example.demo.model.doctor.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);

    @Query("select d from DOCTORS d where d.email in :emails")
    Set<Doctor> findAllByEmails(List<String> emails);

    boolean existsByEmail(String email);
}

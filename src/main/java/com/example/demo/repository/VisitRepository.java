package com.example.demo.repository;

import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.visit.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long>, JpaSpecificationExecutor<Visit> {
    @Query("select count(v) > 0 from VISITS v where v.startTime <= :endTime and v.endTime >= :startTime and v.doctor = :doctor")
    boolean existsBetweenDatesInclusive(OffsetDateTime startTime, OffsetDateTime endTime, Doctor doctor);

    @Modifying
    @Query("update VISITS v set v.patient = null where v.patient.id = :patientId")
    void detachPatientIdFromVisits(Long patientId);

    @Modifying
    @Query("update VISITS v set v.doctor = null where v.doctor.id = :doctorId")
    void detachDoctorIdFromVisits(Long doctorId);

    Page<Visit> findAllByDoctorId(Long doctorId, Pageable pageable);

    Page<Visit> findAllByPatientId(Long patientId, Pageable pageable);
}

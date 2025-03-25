package com.example.demo.repository;

import com.example.demo.model.facility.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
    boolean existsByName(String name);

    @Query("select f from FACILITIES f where f.id in :ids")
    List<Facility> findFacilitiesByIds(List<Long> ids);
}

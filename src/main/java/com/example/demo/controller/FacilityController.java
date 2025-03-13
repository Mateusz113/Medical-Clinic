package com.example.demo.controller;

import com.example.demo.model.PageableContentDto;
import com.example.demo.model.facility.FacilityDTO;
import com.example.demo.model.facility.FullFacilityDataDTO;
import com.example.demo.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/facilities")
@RequiredArgsConstructor
public class FacilityController {
    private final FacilityService facilityService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public FacilityDTO createFacilities(@RequestBody FullFacilityDataDTO facilityData) {
        return facilityService.createFacility(facilityData);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/bulk")
    public List<FacilityDTO> createFacilities(@RequestBody List<FullFacilityDataDTO> facilitiesData) {
        return facilityService.createFacilities(facilitiesData);
    }

    @GetMapping
    public PageableContentDto<FacilityDTO> getFacilities(Pageable pageable) {
        return facilityService.getFacilities(pageable);
    }

    @GetMapping("/{id}")
    public FacilityDTO getFacilityById(@PathVariable("id") Long id) {
        return facilityService.getFacilityById(id);
    }

    @PutMapping("/{id}")
    public FacilityDTO editFacility(@PathVariable("id") Long id, @RequestBody FullFacilityDataDTO facilityData) {
        return facilityService.editFacility(id, facilityData);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteFacility(@PathVariable("id") Long id) {
        facilityService.deleteFacility(id);
    }
}

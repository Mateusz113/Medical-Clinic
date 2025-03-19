package com.example.demo.controller;

import com.example.demo.model.PageableContentDto;
import com.example.demo.model.visit.VisitCreationDTO;
import com.example.demo.model.visit.VisitDTO;
import com.example.demo.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitController {
    private final VisitService visitService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public VisitDTO createVisit(@RequestBody VisitCreationDTO visitData) {
        return visitService.createVisit(visitData);
    }

    @GetMapping
    public PageableContentDto<VisitDTO> getAllVisits(Pageable pageable) {
        return visitService.getAllVisits(pageable);
    }

    @GetMapping("/doctorId/{doctorId}")
    public PageableContentDto<VisitDTO> getDoctorVisits(@PathVariable("doctorId") Long doctorId, Pageable pageable) {
        return visitService.getDoctorVisits(doctorId, pageable);
    }

    @GetMapping("/patientId/{patientId}")
    public PageableContentDto<VisitDTO> getPatientVisits(@PathVariable("patientId") Long patientId, Pageable pageable) {
        return visitService.getPatientVisits(patientId, pageable);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{id}/patientId/{patientId}")
    public void allocatePatientToVisit(@PathVariable("id") Long visitId, @PathVariable("patientId") Long patientId) {
        visitService.allocatePatientToVisit(visitId, patientId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteVisit(@PathVariable("id") Long visitId) {
        visitService.deleteVisit(visitId);
    }
}

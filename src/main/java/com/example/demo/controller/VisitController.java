package com.example.demo.controller;

import com.example.demo.command.visit.InsertVisitCommand;
import com.example.demo.filter.visit.VisitFilter;
import com.example.demo.model.PageableContentDto;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitController {
    private final VisitService visitService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public VisitDTO createVisit(@RequestBody InsertVisitCommand insertVisitCommand) {
        return visitService.createVisit(insertVisitCommand);
    }

    @GetMapping
    public PageableContentDto<VisitDTO> getVisits(VisitFilter visitFilter, Pageable pageable) {
        return visitService.getVisits(visitFilter, pageable);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{id}/patient")
    public void registerPatientToVisit(@PathVariable("id") Long visitId, @RequestParam("patientId") Long patientId) {
        visitService.registerPatientToVisit(visitId, patientId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteVisit(@PathVariable("id") Long visitId) {
        visitService.deleteVisit(visitId);
    }
}

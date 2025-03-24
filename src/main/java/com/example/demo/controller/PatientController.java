package com.example.demo.controller;

import com.example.demo.command.patient.UpsertPatientCommand;
import com.example.demo.model.PageableContentDto;
import com.example.demo.model.PasswordChangeForm;
import com.example.demo.model.patient.PatientDTO;
import com.example.demo.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    public PageableContentDto<PatientDTO> getPatients(Pageable pageable) {
        return patientService.getAllPatients(pageable);
    }

    @GetMapping("/{email}")
    public PatientDTO getPatient(@PathVariable("email") String email) {
        return patientService.getPatient(email);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PatientDTO createPatient(@RequestBody UpsertPatientCommand upsertPatientCommand) {
        return patientService.createPatient(upsertPatientCommand);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{email}")
    public void deletePatient(@PathVariable("email") String email) {
        patientService.deletePatient(email);
    }

    @PutMapping("/{email}")
    public PatientDTO editPatient(@PathVariable("email") String email, @RequestBody UpsertPatientCommand upsertPatientCommand) {
        return patientService.editPatient(email, upsertPatientCommand);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{email}/password")
    public void editPatientPassword(@PathVariable("email") String email, @RequestBody PasswordChangeForm passwordChangeForm) {
        patientService.editPatientPassword(email, passwordChangeForm.password());
    }
}

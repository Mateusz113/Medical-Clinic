package com.example.demo.controller;

import com.example.demo.model.FullPatientDataDTO;
import com.example.demo.model.PasswordChangeForm;
import com.example.demo.model.PatientDTO;
import com.example.demo.service.PatientService;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    public List<PatientDTO> getPatients() {
        return patientService.getAllPatients();
    }

    @GetMapping("/{email}")
    public PatientDTO getPatient(@PathVariable("email") String email) {
        return patientService.getPatient(email);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PatientDTO createPatient(@RequestBody FullPatientDataDTO patientData) {
        return patientService.createPatient(patientData);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{email}")
    public void deletePatient(@PathVariable("email") String email) {
        patientService.deletePatient(email);
    }

    @PutMapping("/{email}")
    public PatientDTO editPatient(@PathVariable("email") String email, @RequestBody FullPatientDataDTO patientData) {
        return patientService.editPatient(email, patientData);
    }

    @PatchMapping("/{email}/password")
    public PatientDTO editPatientPassword(@PathVariable("email") String email, @RequestBody PasswordChangeForm passwordChangeForm) {
        return patientService.editPatientPassword(email, passwordChangeForm.password());
    }
}

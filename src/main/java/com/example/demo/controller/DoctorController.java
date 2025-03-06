package com.example.demo.controller;

import com.example.demo.model.doctor.DoctorDTO;
import com.example.demo.model.doctor.FullDoctorDataDTO;
import com.example.demo.service.DoctorService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public DoctorDTO createDoctor(@RequestBody FullDoctorDataDTO doctorData) {
        return doctorService.createDoctor(doctorData);
    }

    @GetMapping
    public List<DoctorDTO> getDoctors() {
        return doctorService.getDoctors();
    }

    @GetMapping("/{email}")
    public DoctorDTO getDoctorByEmail(@PathVariable("email") String email) {
        return doctorService.getDoctorByEmail(email);
    }

    @PutMapping("/{email}")
    public DoctorDTO editDoctor(@PathVariable("email") String email, @RequestBody FullDoctorDataDTO doctorData) {
        return doctorService.editDoctor(email, doctorData);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{email}/add-facility")
    public void addFacility(@PathVariable("email") String email, @RequestParam("id") Long id) {
        doctorService.addFacility(email, id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{email}/remove-facility")
    public void removeFacility(@PathVariable("email") String email, @RequestParam("id") Long id) {
        doctorService.removeFacility(email, id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{email}")
    public void deleteDoctor(@PathVariable("email") String email) {
        doctorService.deleteDoctor(email);
    }
}

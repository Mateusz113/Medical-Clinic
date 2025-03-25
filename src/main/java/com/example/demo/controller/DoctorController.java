package com.example.demo.controller;

import com.example.demo.command.doctor.UpdateDoctorFacilitiesCommand;
import com.example.demo.command.doctor.UpsertDoctorCommand;
import com.example.demo.model.PageableContentDto;
import com.example.demo.model.doctor.DoctorDTO;
import com.example.demo.service.DoctorService;
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
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public DoctorDTO createDoctor(@RequestBody UpsertDoctorCommand upsertDoctorCommand) {
        return doctorService.createDoctor(upsertDoctorCommand);
    }

    @GetMapping
    public PageableContentDto<DoctorDTO> getDoctors(Pageable pageable) {
        return doctorService.getDoctors(pageable);
    }

    @GetMapping("/{email}")
    public DoctorDTO getDoctorByEmail(@PathVariable("email") String email) {
        return doctorService.getDoctorByEmail(email);
    }

    @PutMapping("/{email}")
    public DoctorDTO editDoctor(@PathVariable("email") String email, @RequestBody UpsertDoctorCommand upsertDoctorCommand) {
        return doctorService.editDoctor(email, upsertDoctorCommand);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{email}/facilities")
    public void editFacilities(@PathVariable("email") String email, @RequestBody UpdateDoctorFacilitiesCommand updateDoctorFacilitiesCommand) {
        doctorService.updateFacilities(email, updateDoctorFacilitiesCommand.facilitiesIds());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{email}")
    public void deleteDoctor(@PathVariable("email") String email) {
        doctorService.deleteDoctor(email);
    }
}

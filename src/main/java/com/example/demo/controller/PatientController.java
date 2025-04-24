package com.example.demo.controller;

import com.example.demo.command.patient.UpdatePatientPasswordCommand;
import com.example.demo.command.patient.UpsertPatientCommand;
import com.example.demo.model.ErrorMessage;
import com.example.demo.model.PageableContentDto;
import com.example.demo.model.patient.PatientDTO;
import com.example.demo.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Patient Operations")
public class PatientController {
    private final PatientService patientService;

    @Operation(summary = "Get all patients with paging")
    @ApiResponse(
            responseCode = "200",
            description = "Returns the paged patients",
            useReturnTypeSchema = true
    )
    @GetMapping
    public PageableContentDto<PatientDTO> getPatients(Pageable pageable) {
        return patientService.getAllPatients(pageable);
    }


    @Operation(summary = "Get patient data by email")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the patient",
                    useReturnTypeSchema = true
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient was not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @GetMapping("/{email}")
    public PatientDTO getPatient(@Parameter(description = "Email of the patient to be searched") @PathVariable("email") String email) {
        return patientService.getPatient(email);
    }

    @Operation(summary = "Create new patient")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created the patient",
                    useReturnTypeSchema = true
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid patient data provided",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Provided email is already taken",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PatientDTO createPatient(@RequestBody UpsertPatientCommand upsertPatientCommand) {
        return patientService.createPatient(upsertPatientCommand);
    }

    @Operation(summary = "Delete patient")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Deleted the patient"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient with provided email not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{email}")
    public void deletePatient(@Parameter(description = "Email of the patient to delete") @PathVariable("email") String email) {
        patientService.deletePatient(email);
    }

    @Operation(summary = "Edit patient")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Edited the patient",
                    useReturnTypeSchema = true
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid patient data provided or new id card number",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient with provided email not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Provided email is different from current one and already taken",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @PutMapping("/{email}")
    public PatientDTO editPatient(@Parameter(description = "Email of the patient to be updated") @PathVariable("email") String email,
                                  @RequestBody UpsertPatientCommand upsertPatientCommand
    ) {
        return patientService.editPatient(email, upsertPatientCommand);
    }

    @Operation(summary = "Edit patient password")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Edited the patient password"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid patient password provided",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient with provided email not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{email}/password")
    public void editPatientPassword(
            @Parameter(description = "Email of the patient to be updated") @PathVariable("email") String email,
            @RequestBody UpdatePatientPasswordCommand updatePatientPasswordCommand
    ) {
        patientService.editPatientPassword(email, updatePatientPasswordCommand.password());
    }
}

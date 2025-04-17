package com.example.demo.controller;

import com.example.demo.command.doctor.UpdateDoctorFacilitiesCommand;
import com.example.demo.command.doctor.UpsertDoctorCommand;
import com.example.demo.model.ErrorMessage;
import com.example.demo.model.PageableContentDto;
import com.example.demo.model.doctor.DoctorDTO;
import com.example.demo.service.DoctorService;
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
@RequestMapping("/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor Operations")
public class DoctorController {
    private final DoctorService doctorService;

    @Operation(summary = "Create new doctor")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created the doctor",
                    useReturnTypeSchema = true
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid doctor data provided",
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
    public DoctorDTO createDoctor(@RequestBody UpsertDoctorCommand upsertDoctorCommand) {
        return doctorService.createDoctor(upsertDoctorCommand);
    }

    @Operation(summary = "Get all doctors with paging")
    @ApiResponse(
            responseCode = "200",
            description = "Returns the paged doctors",
            useReturnTypeSchema = true
    )
    @GetMapping
    public PageableContentDto<DoctorDTO> getDoctors(Pageable pageable) {
        return doctorService.getDoctors(pageable);
    }

    @Operation(summary = "Get doctor data by email")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the doctor",
                    useReturnTypeSchema = true
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor was not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @GetMapping("/{email}")
    public DoctorDTO getDoctorByEmail(@Parameter(description = "Email of the doctor to be searched") @PathVariable("email") String email) {
        return doctorService.getDoctorByEmail(email);
    }

    @Operation(summary = "Edit doctor")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Edited the doctor",
                    useReturnTypeSchema = true
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid doctor data provided",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor with provided email not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Provided email is different from current one and already taken",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @PutMapping("/{email}")
    public DoctorDTO editDoctor(
            @Parameter(description = "Email of the doctor to be searched") @PathVariable("email") String email,
            @RequestBody UpsertDoctorCommand upsertDoctorCommand
    ) {
        return doctorService.editDoctor(email, upsertDoctorCommand);
    }

    @Operation(summary = "Edit doctor facilities")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Edited the doctor"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid doctor data provided",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor with provided email or facilities with provided ids not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{email}/facilities")
    public void editFacilities(
            @Parameter(description = "Email of the doctor to be searched") @PathVariable("email") String email,
            @RequestBody UpdateDoctorFacilitiesCommand updateDoctorFacilitiesCommand
    ) {
        doctorService.updateFacilities(email, updateDoctorFacilitiesCommand.facilitiesIds());
    }

    @Operation(summary = "Delete doctor")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Delete the doctor"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor with provided email not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{email}")
    public void deleteDoctor(@Parameter(description = "Email of the doctor to be searched") @PathVariable("email") String email) {
        doctorService.deleteDoctor(email);
    }
}

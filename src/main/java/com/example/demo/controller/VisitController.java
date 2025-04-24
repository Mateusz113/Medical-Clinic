package com.example.demo.controller;

import com.example.demo.command.visit.InsertVisitCommand;
import com.example.demo.filter.visit.VisitFilter;
import com.example.demo.model.ErrorMessage;
import com.example.demo.model.PageableContentDto;
import com.example.demo.model.visit.VisitDTO;
import com.example.demo.service.VisitService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
@Tag(name = "Visit Operations")
public class VisitController {
    private final VisitService visitService;

    @Operation(summary = "Create new visit")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created the visit",
                    useReturnTypeSchema = true
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid visit data provided",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor with provided email not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public VisitDTO createVisit(@RequestBody InsertVisitCommand insertVisitCommand) {
        return visitService.createVisit(insertVisitCommand);
    }

    @Operation(summary = "Get all visits")
    @ApiResponse(
            responseCode = "200",
            description = "Returns paged visits"
    )
    @GetMapping
    public PageableContentDto<VisitDTO> getVisits(
            @Parameter(description = "Optional visit filters") VisitFilter visitFilter,
            Pageable pageable
    ) {
        return visitService.getVisits(visitFilter, pageable);
    }

    @Operation(summary = "Register patient to visit")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Registered the patient to visit"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Visit is not available",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient with provided id or visit with provided id not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{visitId}/patient/{patientId}")
    public void registerPatientToVisit(
            @Parameter(description = "Id of the visit to be updated") @PathVariable("visitId") Long visitId,
            @Parameter(description = "Id of the patient to be registered") @PathVariable("patientId") Long patientId
    ) {
        visitService.registerPatientToVisit(visitId, patientId);
    }

    @Operation(summary = "Delete visit")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Deleted visit"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Visit is not available",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Visit with provided id not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteVisit(@Parameter(description = "Id of the visit to be deleted") @PathVariable("id") Long visitId) {
        visitService.deleteVisit(visitId);
    }
}

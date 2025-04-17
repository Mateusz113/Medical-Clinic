package com.example.demo.controller;

import com.example.demo.command.facility.InsertFacilityCommand;
import com.example.demo.command.facility.UpdateFacilityCommand;
import com.example.demo.model.ErrorMessage;
import com.example.demo.model.PageableContentDto;
import com.example.demo.model.facility.FacilityDTO;
import com.example.demo.service.FacilityService;
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
@Tag(name = "Facility Operations")
public class FacilityController {
    private final FacilityService facilityService;

    @Operation(summary = "Create new facility")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created the facility",
                    useReturnTypeSchema = true
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid facility or doctor data provided",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Provided name is already taken",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public FacilityDTO createFacility(@RequestBody InsertFacilityCommand insertFacilityCommand) {
        return facilityService.createFacility(insertFacilityCommand);
    }

    @Operation(summary = "Create new facilities")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created the facilities",
                    useReturnTypeSchema = true
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid facility or doctor data provided",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Provided name is already taken",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/bulk")
    public List<FacilityDTO> createFacilities(@RequestBody List<InsertFacilityCommand> insertFacilityCommands) {
        return facilityService.createFacilities(insertFacilityCommands);
    }

    @Operation(summary = "Get all facilities with paging")
    @ApiResponse(
            responseCode = "200",
            description = "Returns the paged facilities",
            useReturnTypeSchema = true
    )
    @GetMapping
    public PageableContentDto<FacilityDTO> getFacilities(Pageable pageable) {
        return facilityService.getFacilities(pageable);
    }

    @Operation(summary = "Get facility by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the facility",
                    useReturnTypeSchema = true
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Facility not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @GetMapping("/{id}")
    public FacilityDTO getFacilityById(@PathVariable("id") Long id) {
        return facilityService.getFacilityById(id);
    }

    @Operation(summary = "Edit facility")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Edited the facility",
                    useReturnTypeSchema = true
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid facility data provided",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Facility with provided id not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Provided name is different from current one and already taken",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @PutMapping("/{id}")
    public FacilityDTO editFacility(
            @Parameter(description = "Id of the facility to be updated") @PathVariable("id") Long id,
            @RequestBody UpdateFacilityCommand updateFacilityCommand
    ) {
        return facilityService.editFacility(id, updateFacilityCommand);
    }

    @Operation(summary = "Delete facility")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Edited the facility",
                    useReturnTypeSchema = true
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Facility with provided id not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteFacility(
            @Parameter(description = "Id of the facility to be deleted") @PathVariable("id") Long id
    ) {
        facilityService.deleteFacility(id);
    }
}

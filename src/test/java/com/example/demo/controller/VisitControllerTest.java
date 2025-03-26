package com.example.demo.controller;

import com.example.demo.command.visit.InsertVisitCommand;
import com.example.demo.exception.visit.VisitIllegalDataException;
import com.example.demo.model.PageableContentDto;
import com.example.demo.model.doctor.SimpleDoctorDTO;
import com.example.demo.model.patient.PatientDTO;
import com.example.demo.model.visit.VisitDTO;
import com.example.demo.service.VisitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class VisitControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private VisitService visitService;
    private Clock clock = Clock.fixed(Instant.parse("2012-12-12T12:00:00Z"), ZoneOffset.UTC);

    @Test
    public void createVisit_DataIsCorrect_ReturnsVisitDtoWithStatus201() throws Exception {
        InsertVisitCommand insertVisitCommand = buildInsertVisitCommand();
        VisitDTO visitDTO = buildVisitDto();
        when(visitService.createVisit(insertVisitCommand)).thenReturn(visitDTO);
        mockMvc.perform(post("/visits")
                        .content(objectMapper.writeValueAsString(insertVisitCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.startTime").value("2012-12-13T12:00:00Z"))
                .andExpect(jsonPath("$.endTime").value("2012-12-13T13:00:00Z"))
                .andExpect(jsonPath("$.doctor.id").value(1))
                .andExpect(jsonPath("$.doctor.email").value("email"))
                .andExpect(jsonPath("$.doctor.firstName").value("firstName"))
                .andExpect(jsonPath("$.doctor.lastName").value("lastName"))
                .andExpect(jsonPath("$.doctor.specialization").value("specialization"))
                .andExpect(jsonPath("$.patient.id").value(1))
                .andExpect(jsonPath("$.patient.email").value("email"))
                .andExpect(jsonPath("$.patient.idCardNo").value("idCardNo"))
                .andExpect(jsonPath("$.patient.firstName").value("firstName"))
                .andExpect(jsonPath("$.patient.lastName").value("lastName"))
                .andExpect(jsonPath("$.patient.phoneNumber").value("phoneNumber"))
                .andExpect(jsonPath("$.patient.birthday").value("2012-12-12"))
                .andDo(print());
    }

    @Test
    public void createVisit_DataIsIncorrect_ReturnsErrorMessageWithStatus400() throws Exception {
        InsertVisitCommand insertVisitCommand = buildInsertVisitCommand();
        VisitDTO visitDTO = buildVisitDto();
        when(visitService.createVisit(insertVisitCommand)).thenThrow(new VisitIllegalDataException("Incorrect data.", OffsetDateTime.now(clock)));
        mockMvc.perform(post("/visits")
                        .content(objectMapper.writeValueAsString(insertVisitCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Incorrect data."))
                .andExpect(jsonPath("$.statusCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.date").value("2012-12-12T12:00Z"))
                .andDo(print());
    }

    @Test
    public void getAllVisits_ThereAreNoVisits_ReturnsEmptyPageableContentDtoWithStatus200() throws Exception {
        PageableContentDto<VisitDTO> pageableContentDto = buildEmptyPageableContentDto();
        Pageable pageable = PageRequest.of(0, 10);
        when(visitService.getAllVisits(pageable)).thenReturn(pageableContentDto);
        mockMvc.perform(get("/visits")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEntries").value(0))
                .andExpect(jsonPath("$.totalNumberOfPages").value(1))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    public void getAllVisits_ThereAreVisits_ReturnsCorrectPageableContentDtoWithStatus200() throws Exception {
        PageableContentDto<VisitDTO> pageableContentDto = buildFullPageableContentDto();
        Pageable pageable = PageRequest.of(0, 10);
        when(visitService.getAllVisits(pageable)).thenReturn(pageableContentDto);
        mockMvc.perform(get("/visits")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEntries").value(2))
                .andExpect(jsonPath("$.totalNumberOfPages").value(1))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].startTime").value("2012-12-13T12:00:00Z"))
                .andExpect(jsonPath("$.content[0].endTime").value("2012-12-13T13:00:00Z"))
                .andExpect(jsonPath("$.content[0].doctor.id").value(1))
                .andExpect(jsonPath("$.content[0].doctor.email").value("email"))
                .andExpect(jsonPath("$.content[0].doctor.firstName").value("firstName"))
                .andExpect(jsonPath("$.content[0].doctor.lastName").value("lastName"))
                .andExpect(jsonPath("$.content[0].doctor.specialization").value("specialization"))
                .andExpect(jsonPath("$.content[0].patient.id").value(1))
                .andExpect(jsonPath("$.content[0].patient.email").value("email"))
                .andExpect(jsonPath("$.content[0].patient.idCardNo").value("idCardNo"))
                .andExpect(jsonPath("$.content[0].patient.firstName").value("firstName"))
                .andExpect(jsonPath("$.content[0].patient.lastName").value("lastName"))
                .andExpect(jsonPath("$.content[0].patient.phoneNumber").value("phoneNumber"))
                .andExpect(jsonPath("$.content[0].patient.birthday").value("2012-12-12"))
                .andExpect(jsonPath("$.content[1].id").value(1))
                .andExpect(jsonPath("$.content[1].startTime").value("2012-12-13T12:00:00Z"))
                .andExpect(jsonPath("$.content[1].endTime").value("2012-12-13T13:00:00Z"))
                .andExpect(jsonPath("$.content[1].doctor.id").value(1))
                .andExpect(jsonPath("$.content[1].doctor.email").value("email"))
                .andExpect(jsonPath("$.content[1].doctor.firstName").value("firstName"))
                .andExpect(jsonPath("$.content[1].doctor.lastName").value("lastName"))
                .andExpect(jsonPath("$.content[1].doctor.specialization").value("specialization"))
                .andExpect(jsonPath("$.content[1].patient.id").value(1))
                .andExpect(jsonPath("$.content[1].patient.email").value("email"))
                .andExpect(jsonPath("$.content[1].patient.idCardNo").value("idCardNo"))
                .andExpect(jsonPath("$.content[1].patient.firstName").value("firstName"))
                .andExpect(jsonPath("$.content[1].patient.lastName").value("lastName"))
                .andExpect(jsonPath("$.content[1].patient.phoneNumber").value("phoneNumber"))
                .andExpect(jsonPath("$.content[1].patient.birthday").value("2012-12-12"))
                .andDo(print());
    }

    @Test
    public void getDoctorVisits_ThereAreVisits_ReturnsCorrectPageableContentDtoWithStatus200() throws Exception {
        long doctorId = 1;
        PageableContentDto<VisitDTO> pageableContentDto = buildFullPageableContentDto();
        Pageable pageable = PageRequest.of(0, 10);
        when(visitService.getDoctorVisits(doctorId, pageable)).thenReturn(pageableContentDto);
        mockMvc.perform(get("/visits/doctorId/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEntries").value(2))
                .andExpect(jsonPath("$.totalNumberOfPages").value(1))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].startTime").value("2012-12-13T12:00:00Z"))
                .andExpect(jsonPath("$.content[0].endTime").value("2012-12-13T13:00:00Z"))
                .andExpect(jsonPath("$.content[0].doctor.id").value(1))
                .andExpect(jsonPath("$.content[0].doctor.email").value("email"))
                .andExpect(jsonPath("$.content[0].doctor.firstName").value("firstName"))
                .andExpect(jsonPath("$.content[0].doctor.lastName").value("lastName"))
                .andExpect(jsonPath("$.content[0].doctor.specialization").value("specialization"))
                .andExpect(jsonPath("$.content[0].patient.id").value(1))
                .andExpect(jsonPath("$.content[0].patient.email").value("email"))
                .andExpect(jsonPath("$.content[0].patient.idCardNo").value("idCardNo"))
                .andExpect(jsonPath("$.content[0].patient.firstName").value("firstName"))
                .andExpect(jsonPath("$.content[0].patient.lastName").value("lastName"))
                .andExpect(jsonPath("$.content[0].patient.phoneNumber").value("phoneNumber"))
                .andExpect(jsonPath("$.content[0].patient.birthday").value("2012-12-12"))
                .andExpect(jsonPath("$.content[1].id").value(1))
                .andExpect(jsonPath("$.content[1].startTime").value("2012-12-13T12:00:00Z"))
                .andExpect(jsonPath("$.content[1].endTime").value("2012-12-13T13:00:00Z"))
                .andExpect(jsonPath("$.content[1].doctor.id").value(1))
                .andExpect(jsonPath("$.content[1].doctor.email").value("email"))
                .andExpect(jsonPath("$.content[1].doctor.firstName").value("firstName"))
                .andExpect(jsonPath("$.content[1].doctor.lastName").value("lastName"))
                .andExpect(jsonPath("$.content[1].doctor.specialization").value("specialization"))
                .andExpect(jsonPath("$.content[1].patient.id").value(1))
                .andExpect(jsonPath("$.content[1].patient.email").value("email"))
                .andExpect(jsonPath("$.content[1].patient.idCardNo").value("idCardNo"))
                .andExpect(jsonPath("$.content[1].patient.firstName").value("firstName"))
                .andExpect(jsonPath("$.content[1].patient.lastName").value("lastName"))
                .andExpect(jsonPath("$.content[1].patient.phoneNumber").value("phoneNumber"))
                .andExpect(jsonPath("$.content[1].patient.birthday").value("2012-12-12"))
                .andDo(print());
    }

    @Test
    public void getPatientVisits_ThereAreVisits_ReturnsCorrectPageableContentDtoWithStatus200() throws Exception {
        long patientId = 1;
        PageableContentDto<VisitDTO> pageableContentDto = buildFullPageableContentDto();
        Pageable pageable = PageRequest.of(0, 10);
        when(visitService.getPatientVisits(patientId, pageable)).thenReturn(pageableContentDto);
        mockMvc.perform(get("/visits/patientId/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEntries").value(2))
                .andExpect(jsonPath("$.totalNumberOfPages").value(1))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].startTime").value("2012-12-13T12:00:00Z"))
                .andExpect(jsonPath("$.content[0].endTime").value("2012-12-13T13:00:00Z"))
                .andExpect(jsonPath("$.content[0].doctor.id").value(1))
                .andExpect(jsonPath("$.content[0].doctor.email").value("email"))
                .andExpect(jsonPath("$.content[0].doctor.firstName").value("firstName"))
                .andExpect(jsonPath("$.content[0].doctor.lastName").value("lastName"))
                .andExpect(jsonPath("$.content[0].doctor.specialization").value("specialization"))
                .andExpect(jsonPath("$.content[0].patient.id").value(1))
                .andExpect(jsonPath("$.content[0].patient.email").value("email"))
                .andExpect(jsonPath("$.content[0].patient.idCardNo").value("idCardNo"))
                .andExpect(jsonPath("$.content[0].patient.firstName").value("firstName"))
                .andExpect(jsonPath("$.content[0].patient.lastName").value("lastName"))
                .andExpect(jsonPath("$.content[0].patient.phoneNumber").value("phoneNumber"))
                .andExpect(jsonPath("$.content[0].patient.birthday").value("2012-12-12"))
                .andExpect(jsonPath("$.content[1].id").value(1))
                .andExpect(jsonPath("$.content[1].startTime").value("2012-12-13T12:00:00Z"))
                .andExpect(jsonPath("$.content[1].endTime").value("2012-12-13T13:00:00Z"))
                .andExpect(jsonPath("$.content[1].doctor.id").value(1))
                .andExpect(jsonPath("$.content[1].doctor.email").value("email"))
                .andExpect(jsonPath("$.content[1].doctor.firstName").value("firstName"))
                .andExpect(jsonPath("$.content[1].doctor.lastName").value("lastName"))
                .andExpect(jsonPath("$.content[1].doctor.specialization").value("specialization"))
                .andExpect(jsonPath("$.content[1].patient.id").value(1))
                .andExpect(jsonPath("$.content[1].patient.email").value("email"))
                .andExpect(jsonPath("$.content[1].patient.idCardNo").value("idCardNo"))
                .andExpect(jsonPath("$.content[1].patient.firstName").value("firstName"))
                .andExpect(jsonPath("$.content[1].patient.lastName").value("lastName"))
                .andExpect(jsonPath("$.content[1].patient.phoneNumber").value("phoneNumber"))
                .andExpect(jsonPath("$.content[1].patient.birthday").value("2012-12-12"))
                .andDo(print());
    }

    @Test
    public void registerPatientToVisit_DataIsCorrect_ReturnsNoBodyWithStatus204() throws Exception {
        long visitId = 1;
        long patientId = 1;
        mockMvc.perform(patch("/visits/1/patientId/1"))
                .andExpect(status().isNoContent())
                .andDo(print());
        verify(visitService, times(1)).registerPatientToVisit(visitId, patientId);
    }

    @Test
    public void deleteVisit_VisitExists_ReturnsNoBodyWithStatus204() throws Exception {
        long visitId = 1;
        mockMvc.perform(delete("/visits/1"))
                .andExpect(status().isNoContent())
                .andDo(print());
        verify(visitService, times(1)).deleteVisit(visitId);
    }

    private InsertVisitCommand buildInsertVisitCommand() {
        return InsertVisitCommand.builder()
                .startTime(getStartTime())
                .endTime(getEndTime())
                .doctorId(1L)
                .build();
    }

    private VisitDTO buildVisitDto() {
        return VisitDTO.builder()
                .id(1L)
                .startTime(getStartTime())
                .endTime(getEndTime())
                .doctor(buildSimpleDoctorDto())
                .patient(buildPatientDto())
                .build();
    }

    private SimpleDoctorDTO buildSimpleDoctorDto() {
        return SimpleDoctorDTO.builder()
                .id(1L)
                .email("email")
                .firstName("firstName")
                .lastName("lastName")
                .specialization("specialization")
                .build();
    }

    private PatientDTO buildPatientDto() {
        return PatientDTO.builder()
                .id(1L)
                .email("email")
                .idCardNo("idCardNo")
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("phoneNumber")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();
    }

    private OffsetDateTime getStartTime() {
        return OffsetDateTime.ofInstant(Instant.parse("2012-12-13T12:00:00Z"), ZoneOffset.UTC);
    }

    private OffsetDateTime getEndTime() {
        return OffsetDateTime.ofInstant(Instant.parse("2012-12-13T13:00:00Z"), ZoneOffset.UTC);
    }

    private PageableContentDto<VisitDTO> buildEmptyPageableContentDto() {
        return PageableContentDto.<VisitDTO>builder()
                .totalEntries(0)
                .totalNumberOfPages(1)
                .pageNumber(0)
                .content(List.of())
                .build();
    }

    private PageableContentDto<VisitDTO> buildFullPageableContentDto() {
        return PageableContentDto.<VisitDTO>builder()
                .totalEntries(2)
                .totalNumberOfPages(1)
                .pageNumber(0)
                .content(List.of(buildVisitDto(), buildVisitDto()))
                .build();
    }
}

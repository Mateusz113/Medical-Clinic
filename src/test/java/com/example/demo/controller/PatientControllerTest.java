package com.example.demo.controller;

import com.example.demo.command.patient.UpdatePatientPasswordCommand;
import com.example.demo.command.patient.UpsertPatientCommand;
import com.example.demo.model.PageableContentDto;
import com.example.demo.model.patient.PatientDTO;
import com.example.demo.service.PatientService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private PatientService patientService;

    @Test
    public void getPatients_ThereAreNoPatients_ReturnsEmptyPageableContentDtoWithStatus200() throws Exception {
        PageableContentDto<PatientDTO> pageableContentDto = buildEmptyPageableContentDto();
        Pageable pageable = PageRequest.of(0, 10);
        when(patientService.getAllPatients(pageable)).thenReturn(pageableContentDto);
        mockMvc.perform(get("/patients")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEntries").value(0))
                .andExpect(jsonPath("$.totalNumberOfPages").value(1))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    public void getPatients_ThereArePatients_ReturnsPageableContentDtoWithStatus200() throws Exception {
        PageableContentDto<PatientDTO> pageableContentDto = buildFullPageableContentDto();
        Pageable pageable = PageRequest.of(0, 10);
        when(patientService.getAllPatients(pageable)).thenReturn(pageableContentDto);
        mockMvc.perform(get("/patients")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEntries").value(2))
                .andExpect(jsonPath("$.totalNumberOfPages").value(1))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].email").value("email"))
                .andExpect(jsonPath("$.content[0].idCardNo").value("idCardNo"))
                .andExpect(jsonPath("$.content[0].firstName").value("firstName"))
                .andExpect(jsonPath("$.content[0].lastName").value("lastName"))
                .andExpect(jsonPath("$.content[0].phoneNumber").value("phoneNumber"))
                .andExpect(jsonPath("$.content[0].birthday").value("2012-12-12"))
                .andExpect(jsonPath("$.content[1].id").value(1))
                .andExpect(jsonPath("$.content[1].email").value("email"))
                .andExpect(jsonPath("$.content[1].idCardNo").value("idCardNo"))
                .andExpect(jsonPath("$.content[1].firstName").value("firstName"))
                .andExpect(jsonPath("$.content[1].lastName").value("lastName"))
                .andExpect(jsonPath("$.content[1].phoneNumber").value("phoneNumber"))
                .andExpect(jsonPath("$.content[1].birthday").value("2012-12-12"));
    }

    @Test
    public void getPatient_PatientExists_ReturnsPatientDtoWithStatus200() throws Exception {
        String email = "email";
        when(patientService.getPatient(email)).thenReturn(buildPatientDto());
        mockMvc.perform(get("/patients/email"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email"))
                .andExpect(jsonPath("$.idCardNo").value("idCardNo"))
                .andExpect(jsonPath("$.firstName").value("firstName"))
                .andExpect(jsonPath("$.lastName").value("lastName"))
                .andExpect(jsonPath("$.phoneNumber").value("phoneNumber"))
                .andExpect(jsonPath("$.birthday").value("2012-12-12"));
    }

    @Test
    public void createPatient_CommandIsCorrect_ReturnsPatientDtoWithStatus201() throws Exception {
        UpsertPatientCommand upsertPatientCommand = buildUpsertPatientCommand();
        when(patientService.createPatient(upsertPatientCommand)).thenReturn(buildPatientDto());
        mockMvc.perform(post("/patients")
                        .content(objectMapper.writeValueAsString(upsertPatientCommand))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email"))
                .andExpect(jsonPath("$.idCardNo").value("idCardNo"))
                .andExpect(jsonPath("$.firstName").value("firstName"))
                .andExpect(jsonPath("$.lastName").value("lastName"))
                .andExpect(jsonPath("$.phoneNumber").value("phoneNumber"))
                .andExpect(jsonPath("$.birthday").value("2012-12-12"));
    }

    @Test
    public void deletePatient_PatientExists_ReturnsNoBodyWithStatus204() throws Exception {
        String email = "email";
        mockMvc.perform(delete("/patients/email"))
                .andExpect(status().isNoContent())
                .andDo(print());
        verify(patientService, times(1)).deletePatient(email);
    }

    @Test
    public void editPatient_EditsPatient_ReturnsPatientDtoWithStatus200() throws Exception {
        String email = "email";
        UpsertPatientCommand upsertPatientCommand = buildUpsertPatientCommand();
        PatientDTO patientDTO = buildPatientDto();
        when(patientService.editPatient(email, upsertPatientCommand)).thenReturn(patientDTO);
        mockMvc.perform(put("/patients/email")
                        .content(objectMapper.writeValueAsString(upsertPatientCommand))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email"))
                .andExpect(jsonPath("$.idCardNo").value("idCardNo"))
                .andExpect(jsonPath("$.firstName").value("firstName"))
                .andExpect(jsonPath("$.lastName").value("lastName"))
                .andExpect(jsonPath("$.phoneNumber").value("phoneNumber"))
                .andExpect(jsonPath("$.birthday").value("2012-12-12"));
    }

    @Test
    public void editPatientPassword_EditsPatientPassword_ReturnsNoBodyWithStatus204() throws Exception {
        String email = "email";
        UpdatePatientPasswordCommand updatePatientPasswordCommand = new UpdatePatientPasswordCommand("password");
        mockMvc.perform(patch("/patients/email/password")
                        .content(objectMapper.writeValueAsString(updatePatientPasswordCommand))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(patientService, times(1)).editPatientPassword(email, updatePatientPasswordCommand.password());
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

    private UpsertPatientCommand buildUpsertPatientCommand() {
        return UpsertPatientCommand.builder()
                .email("email")
                .password("password")
                .idCardNo("idCardNo")
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("phoneNumber")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();
    }

    private PageableContentDto<PatientDTO> buildEmptyPageableContentDto() {
        return PageableContentDto.<PatientDTO>builder()
                .totalEntries(0)
                .totalNumberOfPages(1)
                .pageNumber(0)
                .content(List.of())
                .build();
    }

    private PageableContentDto<PatientDTO> buildFullPageableContentDto() {
        return PageableContentDto.<PatientDTO>builder()
                .totalEntries(2)
                .totalNumberOfPages(1)
                .pageNumber(0)
                .content(List.of(buildPatientDto(), buildPatientDto()))
                .build();
    }
}

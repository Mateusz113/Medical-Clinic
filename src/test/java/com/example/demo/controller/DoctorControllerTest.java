package com.example.demo.controller;

import com.example.demo.command.doctor.UpdateDoctorFacilitiesCommand;
import com.example.demo.command.doctor.UpsertDoctorCommand;
import com.example.demo.model.PageableContentDto;
import com.example.demo.model.doctor.DoctorDTO;
import com.example.demo.service.DoctorService;
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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DoctorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private DoctorService doctorService;

    @Test
    public void createDoctor_ReturnsDoctorDtoWithStatus201() throws Exception {
        DoctorDTO doctorDTO = buildDoctorDto();
        UpsertDoctorCommand upsertDoctorCommand = buildUpsertDoctorCommand();
        when(doctorService.createDoctor(upsertDoctorCommand)).thenReturn(doctorDTO);
        mockMvc.perform(post("/doctors")
                        .content(objectMapper.writeValueAsString(upsertDoctorCommand))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email"))
                .andExpect(jsonPath("$.firstName").value("firstName"))
                .andExpect(jsonPath("$.lastName").value("lastName"))
                .andExpect(jsonPath("$.specialization").value("specialization"))
                .andExpect(jsonPath("$.facilities").isEmpty());
    }

    @Test
    public void getDoctors_ReturnsEmptyPageableContentDtoWithStatus200() throws Exception {
        PageableContentDto<DoctorDTO> pageableContentDto = buildEmptyPageableContentDto();
        Pageable pageable = PageRequest.of(0, 10);
        when(doctorService.getDoctors(pageable)).thenReturn(pageableContentDto);
        mockMvc.perform(get("/doctors")
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
    public void getDoctors_ReturnsPageableContentDtoWithStatus200() throws Exception {
        PageableContentDto<DoctorDTO> pageableContentDto = buildFullPageableContentDto();
        Pageable pageable = PageRequest.of(0, 10);
        when(doctorService.getDoctors(pageable)).thenReturn(pageableContentDto);
        mockMvc.perform(get("/doctors")
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
                .andExpect(jsonPath("$.content[0].firstName").value("firstName"))
                .andExpect(jsonPath("$.content[0].lastName").value("lastName"))
                .andExpect(jsonPath("$.content[0].specialization").value("specialization"))
                .andExpect(jsonPath("$.content[0].facilities").isEmpty())
                .andExpect(jsonPath("$.content[1].id").value(1))
                .andExpect(jsonPath("$.content[1].email").value("email"))
                .andExpect(jsonPath("$.content[1].firstName").value("firstName"))
                .andExpect(jsonPath("$.content[1].lastName").value("lastName"))
                .andExpect(jsonPath("$.content[1].specialization").value("specialization"))
                .andExpect(jsonPath("$.content[1].facilities").isEmpty());
    }

    @Test
    public void getDoctorByEmail_ReturnsDoctorDtoWithStatus200() throws Exception {
        String email = "email";
        DoctorDTO doctorDTO = buildDoctorDto();
        when(doctorService.getDoctorByEmail(email)).thenReturn(doctorDTO);
        mockMvc.perform(get("/doctors/email"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email"))
                .andExpect(jsonPath("$.firstName").value("firstName"))
                .andExpect(jsonPath("$.lastName").value("lastName"))
                .andExpect(jsonPath("$.specialization").value("specialization"))
                .andExpect(jsonPath("$.facilities").isEmpty());
    }

    @Test
    public void editDoctor_ReturnsDoctorDtoWithStatus200() throws Exception {
        String email = "email";
        UpsertDoctorCommand upsertDoctorCommand = buildUpsertDoctorCommand();
        DoctorDTO doctorDTO = buildDoctorDto();
        when(doctorService.editDoctor(email, upsertDoctorCommand)).thenReturn(doctorDTO);
        mockMvc.perform(put("/doctors/email")
                        .content(objectMapper.writeValueAsString(upsertDoctorCommand))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email"))
                .andExpect(jsonPath("$.firstName").value("firstName"))
                .andExpect(jsonPath("$.lastName").value("lastName"))
                .andExpect(jsonPath("$.specialization").value("specialization"))
                .andExpect(jsonPath("$.facilities").isEmpty());
    }

    @Test
    public void editFacilities_ReturnsNoBodyWithStatus204() throws Exception {
        String email = "email";
        UpdateDoctorFacilitiesCommand updateDoctorFacilitiesCommand = buildUpdateDoctorFacilitiesCommand();
        mockMvc.perform(patch("/doctors/email/facilities")
                        .content(objectMapper.writeValueAsString(updateDoctorFacilitiesCommand))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(doctorService, times(1)).updateFacilities(email, updateDoctorFacilitiesCommand.facilitiesIds());
    }

    @Test
    public void deleteDoctor_ReturnsNoBodyWithStatus204() throws Exception {
        String email = "email";
        mockMvc.perform(delete("/doctors/email"))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(doctorService, times(1)).deleteDoctor(email);
    }

    private UpsertDoctorCommand buildUpsertDoctorCommand() {
        return UpsertDoctorCommand.builder()
                .email("email")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .specialization("specialization")
                .build();
    }

    private UpdateDoctorFacilitiesCommand buildUpdateDoctorFacilitiesCommand() {
        return UpdateDoctorFacilitiesCommand.builder()
                .facilitiesIds(List.of(1L, 2L))
                .build();
    }

    private DoctorDTO buildDoctorDto() {
        return DoctorDTO.builder()
                .id(1L)
                .email("email")
                .firstName("firstName")
                .lastName("lastName")
                .specialization("specialization")
                .facilities(List.of())
                .build();
    }

    private PageableContentDto<DoctorDTO> buildEmptyPageableContentDto() {
        return PageableContentDto.<DoctorDTO>builder()
                .totalEntries(0)
                .totalNumberOfPages(1)
                .pageNumber(0)
                .content(List.of())
                .build();
    }

    private PageableContentDto<DoctorDTO> buildFullPageableContentDto() {
        return PageableContentDto.<DoctorDTO>builder()
                .totalEntries(2)
                .totalNumberOfPages(1)
                .pageNumber(0)
                .content(List.of(buildDoctorDto(), buildDoctorDto()))
                .build();
    }
}

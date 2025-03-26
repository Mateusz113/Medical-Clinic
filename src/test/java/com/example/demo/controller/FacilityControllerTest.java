package com.example.demo.controller;

import com.example.demo.command.facility.UpsertFacilityCommand;
import com.example.demo.model.PageableContentDto;
import com.example.demo.model.facility.FacilityDTO;
import com.example.demo.service.FacilityService;
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
public class FacilityControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private FacilityService facilityService;

    @Test
    public void createFacility_ReturnsFacilityDtoWithStatus201() throws Exception {
        UpsertFacilityCommand upsertFacilityCommand = buildUpsertFacilityCommand();
        FacilityDTO facilityDTO = buildFacilityDto();
        when(facilityService.createFacility(upsertFacilityCommand)).thenReturn(facilityDTO);
        mockMvc.perform(post("/facilities")
                        .content(objectMapper.writeValueAsString(upsertFacilityCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.city").value("city"))
                .andExpect(jsonPath("$.zipCode").value("zipCode"))
                .andExpect(jsonPath("$.street").value("street"))
                .andExpect(jsonPath("$.buildingNumber").value("buildingNumber"))
                .andExpect(jsonPath("$.doctors").isEmpty());
    }

    @Test
    public void createFacilities_ReturnsFacilityDtoListWithStatus201() throws Exception {
        List<UpsertFacilityCommand> upsertFacilityCommands = List.of(buildUpsertFacilityCommand(), buildUpsertFacilityCommand());
        List<FacilityDTO> facilityDTOS = List.of(buildFacilityDto(), buildFacilityDto());
        when(facilityService.createFacilities(upsertFacilityCommands)).thenReturn(facilityDTOS);
        mockMvc.perform(post("/facilities/bulk")
                        .content(objectMapper.writeValueAsString(upsertFacilityCommands))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].city").value("city"))
                .andExpect(jsonPath("$[0].zipCode").value("zipCode"))
                .andExpect(jsonPath("$[0].street").value("street"))
                .andExpect(jsonPath("$[0].buildingNumber").value("buildingNumber"))
                .andExpect(jsonPath("$[0].doctors").isEmpty())
                .andExpect(jsonPath("$[1].id").value(1))
                .andExpect(jsonPath("$[1].name").value("name"))
                .andExpect(jsonPath("$[1].city").value("city"))
                .andExpect(jsonPath("$[1].zipCode").value("zipCode"))
                .andExpect(jsonPath("$[1].street").value("street"))
                .andExpect(jsonPath("$[1].buildingNumber").value("buildingNumber"))
                .andExpect(jsonPath("$[1].doctors").isEmpty());
    }

    @Test
    public void getFacilities_ReturnsEmptyPageableContentDtoWithStatus200() throws Exception {
        PageableContentDto<FacilityDTO> pageableContentDto = buildEmptyPageableContentDto();
        Pageable pageable = PageRequest.of(0, 10);
        when(facilityService.getFacilities(pageable)).thenReturn(pageableContentDto);
        mockMvc.perform(get("/facilities")
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
    public void getFacilities_ReturnsCorrectPageableContentDtoWithStatus200() throws Exception {
        PageableContentDto<FacilityDTO> pageableContentDto = buildFullPageableContentDto();
        Pageable pageable = PageRequest.of(0, 10);
        when(facilityService.getFacilities(pageable)).thenReturn(pageableContentDto);
        mockMvc.perform(get("/facilities")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEntries").value(2))
                .andExpect(jsonPath("$.totalNumberOfPages").value(1))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("name"))
                .andExpect(jsonPath("$.content[0].city").value("city"))
                .andExpect(jsonPath("$.content[0].zipCode").value("zipCode"))
                .andExpect(jsonPath("$.content[0].street").value("street"))
                .andExpect(jsonPath("$.content[0].buildingNumber").value("buildingNumber"))
                .andExpect(jsonPath("$.content[0].doctors").isEmpty())
                .andExpect(jsonPath("$.content[1].id").value(1))
                .andExpect(jsonPath("$.content[1].name").value("name"))
                .andExpect(jsonPath("$.content[1].city").value("city"))
                .andExpect(jsonPath("$.content[1].zipCode").value("zipCode"))
                .andExpect(jsonPath("$.content[1].street").value("street"))
                .andExpect(jsonPath("$.content[1].buildingNumber").value("buildingNumber"))
                .andExpect(jsonPath("$.content[1].doctors").isEmpty());
    }

    @Test
    public void editFacility_ReturnsFacilityDtoWithStatus200() throws Exception {
        long facilityId = 1;
        UpsertFacilityCommand upsertFacilityCommand = buildUpsertFacilityCommand();
        FacilityDTO facilityDTO = buildFacilityDto();
        when(facilityService.editFacility(facilityId, upsertFacilityCommand)).thenReturn(facilityDTO);
        mockMvc.perform(put("/facilities/1")
                        .content(objectMapper.writeValueAsString(upsertFacilityCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.city").value("city"))
                .andExpect(jsonPath("$.zipCode").value("zipCode"))
                .andExpect(jsonPath("$.street").value("street"))
                .andExpect(jsonPath("$.buildingNumber").value("buildingNumber"))
                .andExpect(jsonPath("$.doctors").isEmpty());
    }

    @Test
    public void deleteFacility_ReturnsNoBodyWithStatus204() throws Exception {
        long facilityId = 1;
        mockMvc.perform(delete("/facilities/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(facilityService, times(1)).deleteFacility(facilityId);
    }

    private UpsertFacilityCommand buildUpsertFacilityCommand() {
        return UpsertFacilityCommand.builder()
                .name("name")
                .city("city")
                .zipCode("zipCode")
                .street("street")
                .buildingNumber("buildingNumber")
                .doctors(List.of())
                .build();
    }

    private FacilityDTO buildFacilityDto() {
        return FacilityDTO.builder()
                .id(1L)
                .name("name")
                .city("city")
                .zipCode("zipCode")
                .street("street")
                .buildingNumber("buildingNumber")
                .doctors(List.of())
                .build();
    }

    private PageableContentDto<FacilityDTO> buildEmptyPageableContentDto() {
        return PageableContentDto.<FacilityDTO>builder()
                .totalEntries(0)
                .totalNumberOfPages(1)
                .pageNumber(0)
                .content(List.of())
                .build();
    }

    private PageableContentDto<FacilityDTO> buildFullPageableContentDto() {
        return PageableContentDto.<FacilityDTO>builder()
                .totalEntries(2)
                .totalNumberOfPages(1)
                .pageNumber(0)
                .content(List.of(buildFacilityDto(), buildFacilityDto()))
                .build();
    }
}

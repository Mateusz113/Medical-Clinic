package com.example.demo.service;

import com.example.demo.mapper.DoctorMapper;
import com.example.demo.mapper.FacilityMapper;
import com.example.demo.model.PageableContentDto;
import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.facility.Facility;
import com.example.demo.model.facility.FacilityDTO;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.FacilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

public class FacilityServiceTest {
    private FacilityRepository facilityRepository;
    private DoctorRepository doctorRepository;
    private FacilityService facilityService;

    @BeforeEach
    public void setUp() {
        facilityRepository = mock(FacilityRepository.class);
        doctorRepository = mock(DoctorRepository.class);
        FacilityMapper facilityMapper = Mappers.getMapper(FacilityMapper.class);
        DoctorMapper doctorMapper = Mappers.getMapper(DoctorMapper.class);
        Clock clock = Clock.fixed(Instant.parse("2012-12-12T12:00:00Z"), ZoneOffset.UTC);
        facilityService = new FacilityService(facilityRepository, doctorRepository, facilityMapper, doctorMapper, clock);
    }

    @Test
    public void createFacility_FacilityDataHasNullValues_ThrowsFacilityIllegalDataException() {
        assertNull(null);
    }

    @Test
    public void createFacility_FacilityNameAlreadyExists_ThrowsFacilityAlreadyExistsException() {

    }

    @Test
    public void createFacility_DoctorDataHasNullValues_ThrowsDoctorIllegalDataException() {

    }

    @Test
    public void createFacility_DataIsCorrect_CreatesFacility() {

    }

    @Test
    public void getFacilities_ThereAreNoFacilities_ReturnsEmptyPageableContentDto() {

    }

    @Test
    public void getFacilities_ThereAreFacilities_ReturnsCorrectPageableContentDto() {

    }

    @Test
    public void getFacilityById_FacilityDoesNotExist_ThrowsFacilityNotFoundException() {

    }

    @Test
    public void getFacilityById_FacilityExists_ReturnsFacilityDto() {

    }

    @Test
    public void editFacility_FacilityDoesNotExist_ThrowsFacilityNotFoundException(){

    }

    @Test
    public void editFacility_FacilityDataHasNullValues_ThrowsFacilityIllegalDataException(){

    }

    @Test
    public void editFacility_FacilityNewNameIsTakenAndDifferentFromCurrent_ThrowsFacilityAlreadyExistsException(){

    }

    @Test
    public void editFacility_NewDataIsCorrect_EditsFacility(){

    }

    @Test
    public void deleteFacility_FacilityDoesNotExist_ThrowsFacilityNotFoundException(){

    }

    @Test
    public void deleteFacility_FacilityExists_DeletesFacility(){

    }
}

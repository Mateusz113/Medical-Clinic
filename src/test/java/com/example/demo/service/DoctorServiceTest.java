package com.example.demo.service;

import com.example.demo.argument_matcher.DoctorArgumentMatcher;
import com.example.demo.argument_matcher.FacilityArgumentMatcher;
import com.example.demo.command.doctor.UpdateDoctorFacilitiesCommand;
import com.example.demo.command.doctor.UpsertDoctorCommand;
import com.example.demo.exception.doctor.DoctorAlreadyExistsException;
import com.example.demo.exception.doctor.DoctorIllegalDataException;
import com.example.demo.exception.doctor.DoctorNotFoundException;
import com.example.demo.exception.facility.FacilityNotFoundException;
import com.example.demo.mapper.DoctorMapper;
import com.example.demo.model.PageableContentDto;
import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.doctor.DoctorDTO;
import com.example.demo.model.facility.Facility;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.FacilityRepository;
import com.example.demo.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DoctorServiceTest {
    private DoctorRepository doctorRepository;
    private FacilityRepository facilityRepository;
    private DoctorService doctorService;

    @BeforeEach
    void setUp() {
        doctorRepository = mock(DoctorRepository.class);
        facilityRepository = mock(FacilityRepository.class);
        VisitRepository visitRepository = mock(VisitRepository.class);
        DoctorMapper doctorMapper = Mappers.getMapper(DoctorMapper.class);
        Clock clock = Clock.fixed(Instant.parse("2012-12-12T12:00:00Z"), ZoneOffset.UTC);
        doctorService = new DoctorService(doctorRepository, facilityRepository, doctorMapper, visitRepository, clock);
    }

    @ParameterizedTest
    @MethodSource("provideUpsertDoctorCommandsWithNulls")
    public void createDoctor_NewDoctorDataHasNullValues_ThrowsDoctorIllegalDataException(UpsertDoctorCommand upsertDoctorCommand) {
        //given
        //when
        DoctorIllegalDataException exception = assertThrows(DoctorIllegalDataException.class, () -> doctorService.createDoctor(upsertDoctorCommand));
        //then
        assertEquals("There cannot be null fields in doctor.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("2012-12-12T12:00Z", exception.getDate().toString());
    }

    @Test
    public void createDoctor_NewDoctorEmailIsNotAvailable_ThrowsDoctorAlreadyExistsException() {
        //given
        UpsertDoctorCommand upsertDoctorCommand = buildUpsertDoctorCommand();
        when(doctorRepository.existsByEmail(upsertDoctorCommand.email())).thenReturn(true);
        //when
        DoctorAlreadyExistsException exception = assertThrows(DoctorAlreadyExistsException.class, () -> doctorService.createDoctor(upsertDoctorCommand));
        //then
        assertEquals("Doctor with email: %s already exists.".formatted(upsertDoctorCommand.email()), exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("2012-12-12T12:00Z", exception.getDate().toString());
    }

    @Test
    public void createDoctor_NewDoctorDataIsCorrect_ReturnsDoctorDto() {
        //given
        UpsertDoctorCommand upsertDoctorCommand = buildUpsertDoctorCommand();
        Doctor doctor = buildDoctor();
        when(doctorRepository.existsByEmail(upsertDoctorCommand.email())).thenReturn(false);
        when(doctorRepository.save(doctor)).thenReturn(doctor);
        //when
        DoctorDTO result = doctorService.createDoctor(upsertDoctorCommand);
        //then
        assertEquals(1L, result.id());
        assertEquals("email", result.email());
        assertEquals("firstName", result.firstName());
        assertEquals("lastName", result.lastName());
        assertEquals("specialization", result.specialization());
        assertEquals(List.of(), result.facilities());
    }

    @Test
    void getDoctors_ThereAreNoDoctors_ReturnsEmptyPageableContentDto() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        when(doctorRepository.findAll(pageable)).thenReturn(Page.empty());
        //when
        PageableContentDto<DoctorDTO> result = doctorService.getDoctors(pageable);
        //then
        assertEquals(0, result.pageNumber());
        assertEquals(0, result.totalEntries());
        assertEquals(1, result.totalNumberOfPages());
        assertTrue(result.content().isEmpty());
    }

    @Test
    void getDoctors_ThereAreDoctors_ReturnsCorrectPageableContentDto() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        Doctor doctor1 = buildDoctor();
        Doctor doctor2 = buildDoctor();
        doctor2.setId(2L);
        List<Doctor> doctors = List.of(doctor1, doctor2);
        Page<Doctor> doctorPage = new PageImpl<>(doctors, pageable, 2L);
        when(doctorRepository.findAll(pageable)).thenReturn(doctorPage);
        //when
        PageableContentDto<DoctorDTO> result = doctorService.getDoctors(pageable);
        //then
        assertEquals(0, result.pageNumber());
        assertEquals(2, result.totalEntries());
        assertEquals(1, result.totalNumberOfPages());
        assertEquals(2, result.content().size());
        assertEquals(1L, result.content().getFirst().id());
        assertEquals("email", result.content().getFirst().email());
        assertEquals("firstName", result.content().getFirst().firstName());
        assertEquals("lastName", result.content().getFirst().lastName());
        assertEquals("specialization", result.content().getFirst().specialization());
        assertEquals(0, result.content().getFirst().facilities().size());
        assertEquals(2L, result.content().get(1).id());
        assertEquals("email", result.content().get(1).email());
        assertEquals("firstName", result.content().get(1).firstName());
        assertEquals("lastName", result.content().get(1).lastName());
        assertEquals("specialization", result.content().get(1).specialization());
        assertEquals(0, result.content().get(1).facilities().size());
    }

    @Test
    void getDoctorByEmail_DoctorWithEmailDoesNotExist_ThrowsDoctorNotFoundException() {
        //given
        String email = "email";
        when(doctorRepository.findByEmail(email)).thenReturn(Optional.empty());
        //when
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class, () -> doctorService.getDoctorByEmail(email));
        //then
        assertEquals("Doctor with email: %s does not exist.".formatted(email), exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("2012-12-12T12:00Z", exception.getDate().toString());
    }

    @Test
    void getDoctorByEmail_DoctorWithEmailExists_ReturnsDoctorDto() {
        //given
        String email = "email";
        Doctor doctor = buildDoctor();
        when(doctorRepository.findByEmail(email)).thenReturn(Optional.of(doctor));
        //when
        DoctorDTO result = doctorService.getDoctorByEmail(email);
        //then
        assertEquals(1L, result.id());
        assertEquals("email", result.email());
        assertEquals("firstName", result.firstName());
        assertEquals("lastName", result.lastName());
        assertEquals("specialization", result.specialization());
        assertEquals(0, result.facilities().size());
    }

    @Test
    void editDoctor_DoctorWithEmailDoesNotExist_ThrowsDoctorNotFoundException() {
        //given
        String email = "email";
        UpsertDoctorCommand upsertDoctorCommand = buildUpsertDoctorCommand();
        when(doctorRepository.findByEmail(email)).thenReturn(Optional.empty());
        //when
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class, () -> doctorService.editDoctor(email, upsertDoctorCommand));
        //then
        assertEquals("Doctor with email: %s does not exist.".formatted(email), exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("2012-12-12T12:00Z", exception.getDate().toString());
    }

    @ParameterizedTest
    @MethodSource("provideUpsertDoctorCommandsWithNulls")
    void editDoctor_NewDataHasNullValues_ThrowsDoctorIllegalDataException(UpsertDoctorCommand upsertDoctorCommand) {
        //given
        String email = "email";
        Doctor doctor = buildDoctor();
        when(doctorRepository.findByEmail(email)).thenReturn(Optional.of(doctor));
        //when
        DoctorIllegalDataException exception = assertThrows(DoctorIllegalDataException.class, () -> doctorService.editDoctor(email, upsertDoctorCommand));
        //then
        assertEquals("There cannot be null fields in doctor.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("2012-12-12T12:00Z", exception.getDate().toString());
    }

    @Test
    void editDoctor_NewEmailExistsAndIsDifferentFromCurrentEmail_ThrowsDoctorAlreadyExistsException() {
        //given
        String email = "initial email";
        Doctor doctor = buildDoctor();
        doctor.setEmail(email);
        UpsertDoctorCommand upsertDoctorCommand = buildUpsertDoctorCommand();
        when(doctorRepository.findByEmail(email)).thenReturn(Optional.of(doctor));
        when(doctorRepository.existsByEmail(upsertDoctorCommand.email())).thenReturn(true);
        //when
        DoctorAlreadyExistsException exception = assertThrows(DoctorAlreadyExistsException.class, () -> doctorService.editDoctor(email, upsertDoctorCommand));
        //then
        assertEquals("Doctor with email: %s already exists.".formatted(upsertDoctorCommand.email()), exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("2012-12-12T12:00Z", exception.getDate().toString());
    }

    @Test
    void editDoctor_NewDataIsCorrect_UpdatesDoctor() {
        //given
        String email = "initial email";
        UpsertDoctorCommand upsertDoctorCommand = buildUpsertDoctorCommand();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .email(email)
                .password("initial password")
                .firstName("initial firstName")
                .lastName("initial lastName")
                .specialization("initial specialization")
                .facilities(new HashSet<>())
                .build();
        when(doctorRepository.findByEmail(email)).thenReturn(Optional.of(doctor));
        when(doctorRepository.existsByEmail(upsertDoctorCommand.email())).thenReturn(false);
        //when
        DoctorDTO result = doctorService.editDoctor(email, upsertDoctorCommand);
        //then
        assertEquals(1L, result.id());
        assertEquals("email", result.email());
        assertEquals("firstName", result.firstName());
        assertEquals("lastName", result.lastName());
        assertEquals("specialization", result.specialization());
        assertEquals(0, result.facilities().size());
    }

    @Test
    void updateFacilities_DoctorWithEmailDoesNotExist_ThrowsDoctorNotFoundException() {
        //given
        String email = "email";
        List<Long> facilitiesIds = List.of();
        when(doctorRepository.findByEmail(email)).thenReturn(Optional.empty());
        //when
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class, () -> doctorService.updateFacilities(email, facilitiesIds));
        //then
        assertEquals("Doctor with email: %s does not exist.".formatted(email), exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("2012-12-12T12:00Z", exception.getDate().toString());
    }

    @Test
    void updateFacilities_InvalidListOfFacilitiesWasPassed_ThrowsFacilityNotFoundException() {
        //given
        String email = "email";
        List<Long> facilitiesIds = List.of(1L, 2L);
        Doctor doctor = buildDoctor();
        when(doctorRepository.findByEmail(email)).thenReturn(Optional.of(doctor));
        when(facilityRepository.findFacilitiesByIds(facilitiesIds)).thenReturn(List.of());
        //when
        FacilityNotFoundException exception = assertThrows(FacilityNotFoundException.class, () -> doctorService.updateFacilities(email, facilitiesIds));
        //then
        assertEquals("List of facilities ids contained invalid values.", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("2012-12-12T12:00Z", exception.getDate().toString());
    }

    @Test
    void updateFacilities_CorrectDataPassed_UpdatesDoctorFacilitiesList() {
        //given
        String email = "email";
        List<Long> facilitiesIds = List.of(1L, 2L);
        Doctor doctor = buildDoctor();
        Facility facility1 = Facility.builder()
                .id(1L)
                .name("name1")
                .city("city1")
                .zipCode("zipCode1")
                .street("street1")
                .buildingNumber("buildingNumber1")
                .doctors(Set.of())
                .build();
        Facility facility2 = Facility.builder()
                .id(2L)
                .name("name2")
                .city("city2")
                .zipCode("zipCode2")
                .street("street2")
                .buildingNumber("buildingNumber2")
                .doctors(Set.of())
                .build();
        List<Facility> facilities = List.of(facility1, facility2);
        ArgumentCaptor<Doctor> doctorCaptor = ArgumentCaptor.captor();
        when(doctorRepository.findByEmail(email)).thenReturn(Optional.of(doctor));
        when(facilityRepository.findFacilitiesByIds(facilitiesIds)).thenReturn(facilities);
        //when
        doctorService.updateFacilities(email, facilitiesIds);
        //then
        verify(doctorRepository).save(doctorCaptor.capture());
        Doctor result = doctorCaptor.getValue();
        assertEquals(2, result.getFacilities().size());
        assertTrue(result.getFacilities().stream()
                .anyMatch(facility -> new FacilityArgumentMatcher(facility1).matches(facility)));
        assertTrue(result.getFacilities().stream()
                .anyMatch(facility -> new FacilityArgumentMatcher(facility2).matches(facility)));
    }

    @Test
    void deleteDoctor_DoctorWithEmailDoesNotExist_ThrowsDoctorNotFoundException() {
        //given
        String email = "email";
        when(doctorRepository.findByEmail(email)).thenReturn(Optional.empty());
        //when
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class, () -> doctorService.deleteDoctor(email));
        //then
        assertEquals("Doctor with email: %s does not exist.".formatted(email), exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("2012-12-12T12:00Z", exception.getDate().toString());
    }

    @Test
    void deleteDoctor_DoctorWithEmailExists_DeletesDoctor() {
        //given
        String email = "email";
        Doctor doctor = buildDoctor();
        Doctor expected = buildDoctor();
        when(doctorRepository.findByEmail(email)).thenReturn(Optional.of(doctor));
        //when
        doctorService.deleteDoctor(email);
        //then
        verify(doctorRepository, times(1)).delete(argThat(new DoctorArgumentMatcher(expected)));
    }

    private Doctor buildDoctor() {
        return Doctor.builder()
                .id(1L)
                .email("email")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .specialization("specialization")
                .facilities(new HashSet<>())
                .visits(new HashSet<>())
                .build();
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

    private UpsertDoctorCommand getUpsertDoctorCommand(String email, String password, String firstName, String lastName, String specialization) {
        return UpsertDoctorCommand.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .specialization(specialization)
                .build();
    }

    private Stream<Arguments> provideUpsertDoctorCommandsWithNulls() {
        return Stream.of(
                Arguments.of(getUpsertDoctorCommand(null, "password", "firstName", "lastName", "specialization")),
                Arguments.of(getUpsertDoctorCommand("email", null, "firstName", "lastName", "specialization")),
                Arguments.of(getUpsertDoctorCommand("email", "password", null, "lastName", "specialization")),
                Arguments.of(getUpsertDoctorCommand("email", "password", "firstName", null, "specialization")),
                Arguments.of(getUpsertDoctorCommand("email", "password", "firstName", "lastName", null))
        );
    }
}

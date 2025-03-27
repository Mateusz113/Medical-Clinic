package com.example.demo.service;

import com.example.demo.argument_matcher.FacilityArgumentMatcher;
import com.example.demo.command.doctor.UpsertDoctorCommand;
import com.example.demo.command.facility.InsertFacilityCommand;
import com.example.demo.command.facility.UpdateFacilityCommand;
import com.example.demo.exception.doctor.DoctorIllegalDataException;
import com.example.demo.exception.facility.FacilityAlreadyExistsException;
import com.example.demo.exception.facility.FacilityIllegalDataException;
import com.example.demo.exception.facility.FacilityNotFoundException;
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
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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

    @ParameterizedTest
    @MethodSource("provideInsertFacilityCommandsWithNulls")
    public void createFacility_FacilityDataHasNullValues_ThrowsFacilityIllegalDataException(InsertFacilityCommand insertFacilityCommand) {
        FacilityIllegalDataException exception = assertThrows(FacilityIllegalDataException.class, () -> facilityService.createFacility(insertFacilityCommand));

        assertEquals("There cannot be null fields in facility.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(getDefaultErrorTimeString(), exception.getDate().toString());
    }

    @Test
    public void createFacility_FacilityNameAlreadyExists_ThrowsFacilityAlreadyExistsException() {
        InsertFacilityCommand insertFacilityCommand = buildInsertFacilityCommand();
        when(facilityRepository.existsByName(insertFacilityCommand.name())).thenReturn(true);

        FacilityAlreadyExistsException exception = assertThrows(FacilityAlreadyExistsException.class, () -> facilityService.createFacility(insertFacilityCommand));

        assertEquals("Facility with name: %s already exists.".formatted(insertFacilityCommand.name()), exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals(getDefaultErrorTimeString(), exception.getDate().toString());
    }

    @ParameterizedTest
    @MethodSource("provideUpsertFacilityCommandsUpsertDoctorCommandsWithNulls")
    public void createFacility_DoctorDataHasNullValues_ThrowsDoctorIllegalDataException(InsertFacilityCommand insertFacilityCommand) {
        when(facilityRepository.existsByName(insertFacilityCommand.name())).thenReturn(false);

        DoctorIllegalDataException exception = assertThrows(DoctorIllegalDataException.class, () -> facilityService.createFacility(insertFacilityCommand));

        assertEquals("There cannot be null fields in doctor.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(getDefaultErrorTimeString(), exception.getDate().toString());
    }

    @Test
    public void createFacility_DataIsCorrect_CreatesFacility() {
        InsertFacilityCommand insertFacilityCommand = buildInsertFacilityCommand();
        List<String> requestDoctorEmails = insertFacilityCommand.doctors().stream()
                .map(UpsertDoctorCommand::email)
                .toList();
        Facility facility = buildFacility();
        Doctor doctor = buildDoctor();
        doctor.addFacility(facility);
        facility.setDoctors(Set.of(doctor));
        FacilityArgumentMatcher facilityArgumentMatcher = new FacilityArgumentMatcher(facility);
        when(facilityRepository.existsByName(insertFacilityCommand.name())).thenReturn(false);
        when(doctorRepository.findAllByEmails(requestDoctorEmails)).thenReturn(Set.of());
        when(facilityRepository.save(argThat(facilityArgumentMatcher))).thenReturn(facility);

        FacilityDTO result = facilityService.createFacility(insertFacilityCommand);

        assertNull(result.id());
        assertEquals("name", result.name());
        assertEquals("city", result.city());
        assertEquals("zipCode", result.zipCode());
        assertEquals("street", result.street());
        assertEquals("buildingNumber", result.buildingNumber());
        assertEquals(1, result.doctors().size());
        assertNull(result.doctors().getFirst().id());
        assertEquals("email", result.doctors().getFirst().email());
        assertEquals("firstName", result.doctors().getFirst().firstName());
        assertEquals("lastName", result.doctors().getFirst().lastName());
        assertEquals("specialization", result.doctors().getFirst().specialization());
    }

    @Test
    public void getFacilities_ThereAreNoFacilities_ReturnsEmptyPageableContentDto() {
        Pageable pageable = PageRequest.of(0, 10);
        when(facilityRepository.findAll(pageable)).thenReturn(Page.empty());

        PageableContentDto<FacilityDTO> result = facilityService.getFacilities(pageable);

        assertEquals(0, result.pageNumber());
        assertEquals(0, result.totalEntries());
        assertEquals(1, result.totalNumberOfPages());
        assertTrue(result.content().isEmpty());
    }

    @Test
    public void getFacilities_ThereAreFacilities_ReturnsCorrectPageableContentDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Facility facility1 = buildFacility();
        Facility facility2 = buildFacility();
        facility1.setId(1L);
        facility2.setId(2L);
        List<Facility> facilities = List.of(facility1, facility2);
        Page<Facility> facilityPage = new PageImpl<>(facilities, pageable, 2L);
        when(facilityRepository.findAll(pageable)).thenReturn(facilityPage);

        PageableContentDto<FacilityDTO> result = facilityService.getFacilities(pageable);

        assertEquals(0, result.pageNumber());
        assertEquals(2, result.totalEntries());
        assertEquals(1, result.totalNumberOfPages());
        assertEquals(2, result.content().size());
        assertEquals(1L, result.content().getFirst().id());
        assertEquals("name", result.content().getFirst().name());
        assertEquals("city", result.content().getFirst().city());
        assertEquals("zipCode", result.content().getFirst().zipCode());
        assertEquals("street", result.content().getFirst().street());
        assertEquals("buildingNumber", result.content().getFirst().buildingNumber());
        assertEquals(0, result.content().getFirst().doctors().size());
        assertEquals(2L, result.content().get(1).id());
        assertEquals("name", result.content().get(1).name());
        assertEquals("city", result.content().get(1).city());
        assertEquals("zipCode", result.content().get(1).zipCode());
        assertEquals("street", result.content().get(1).street());
        assertEquals("buildingNumber", result.content().get(1).buildingNumber());
        assertEquals(0, result.content().get(1).doctors().size());
    }

    @Test
    public void getFacilityById_FacilityDoesNotExist_ThrowsFacilityNotFoundException() {
        long facilityId = 1;
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());

        FacilityNotFoundException exception = assertThrows(FacilityNotFoundException.class, () -> facilityService.getFacilityById(facilityId));

        assertEquals("Facility with id: %d does not exist.".formatted(facilityId), exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(getDefaultErrorTimeString(), exception.getDate().toString());
    }

    @Test
    public void getFacilityById_FacilityExists_ReturnsFacilityDto() {
        long facilityId = 1;
        Facility facility = buildFacility();
        facility.setId(facilityId);
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facility));

        FacilityDTO result = facilityService.getFacilityById(facilityId);

        assertEquals(1L, result.id());
        assertEquals("name", result.name());
        assertEquals("city", result.city());
        assertEquals("zipCode", result.zipCode());
        assertEquals("street", result.street());
        assertEquals("buildingNumber", result.buildingNumber());
        assertEquals(0, result.doctors().size());
    }

    @Test
    public void editFacility_FacilityDoesNotExist_ThrowsFacilityNotFoundException() {
        long facilityId = 1;
        UpdateFacilityCommand updateFacilityCommand = buildUpdateFacilityCommand();
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());

        FacilityNotFoundException exception = assertThrows(FacilityNotFoundException.class, () -> facilityService.editFacility(facilityId, updateFacilityCommand));

        assertEquals("Facility with id: %d does not exist.".formatted(facilityId), exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(getDefaultErrorTimeString(), exception.getDate().toString());
    }

    @ParameterizedTest
    @MethodSource("provideUpdateFacilityCommandsWithNulls")
    public void editFacility_FacilityDataHasNullValues_ThrowsFacilityIllegalDataException(UpdateFacilityCommand updateFacilityCommand) {
        long facilityId = 1;
        Facility facility = buildFacility();
        facility.setId(facilityId);
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facility));

        FacilityIllegalDataException exception = assertThrows(FacilityIllegalDataException.class, () -> facilityService.editFacility(facilityId, updateFacilityCommand));

        assertEquals("There cannot be null fields in facility.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(getDefaultErrorTimeString(), exception.getDate().toString());
    }

    @Test
    public void editFacility_FacilityNewNameIsTakenAndDifferentFromCurrent_ThrowsFacilityAlreadyExistsException() {
        long facilityId = 1;
        UpdateFacilityCommand updateFacilityCommand = buildUpdateFacilityCommand();
        Facility facility = buildFacility();
        facility.setId(facilityId);
        facility.setName("initial name");
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facility));
        when(facilityRepository.existsByName(updateFacilityCommand.name())).thenReturn(true);

        FacilityAlreadyExistsException exception = assertThrows(FacilityAlreadyExistsException.class, () -> facilityService.editFacility(facilityId, updateFacilityCommand));

        assertEquals("Facility with name: %s already exists.".formatted(updateFacilityCommand.name()), exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals(getDefaultErrorTimeString(), exception.getDate().toString());
    }

    @Test
    public void editFacility_NewDataIsCorrect_EditsFacility() {
        long facilityId = 1;
        UpdateFacilityCommand updateFacilityCommand = buildUpdateFacilityCommand();
        Facility facility = buildFacility();
        facility.setId(facilityId);
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facility));
        when(facilityRepository.existsByName(updateFacilityCommand.name())).thenReturn(false);

        FacilityDTO result = facilityService.editFacility(facilityId, updateFacilityCommand);

        assertEquals(1L, result.id());
        assertEquals("name", result.name());
        assertEquals("city", result.city());
        assertEquals("zipCode", result.zipCode());
        assertEquals("street", result.street());
        assertEquals("buildingNumber", result.buildingNumber());
        assertEquals(0, result.doctors().size());
    }

    @Test
    public void deleteFacility_FacilityDoesNotExist_ThrowsFacilityNotFoundException() {
        long facilityId = 1;
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());

        FacilityNotFoundException exception = assertThrows(FacilityNotFoundException.class, () -> facilityService.deleteFacility(facilityId));

        assertEquals("Facility with id: %d does not exist.".formatted(facilityId), exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(getDefaultErrorTimeString(), exception.getDate().toString());
    }

    @Test
    public void deleteFacility_FacilityExists_DeletesFacility() {
        long facilityId = 1;
        Facility facility = buildFacility();
        FacilityArgumentMatcher facilityArgumentMatcher = new FacilityArgumentMatcher(facility);
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facility));

        facilityService.deleteFacility(facilityId);

        verify(facilityRepository, times(1)).delete(argThat(facilityArgumentMatcher));
    }

    private Facility buildFacility() {
        return Facility.builder()
                .id(null)
                .name("name")
                .city("city")
                .zipCode("zipCode")
                .street("street")
                .buildingNumber("buildingNumber")
                .doctors(Set.of())
                .build();
    }

    private Doctor buildDoctor() {
        return Doctor.builder()
                .id(null)
                .email("email")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .specialization("specialization")
                .facilities(new HashSet<>())
                .visits(new HashSet<>())
                .build();
    }

    private InsertFacilityCommand buildInsertFacilityCommand() {
        return InsertFacilityCommand.builder()
                .name("name")
                .city("city")
                .zipCode("zipCode")
                .street("street")
                .buildingNumber("buildingNumber")
                .doctors(List.of(buildUpsertDoctorCommand()))
                .build();
    }

    private InsertFacilityCommand buildInsertFacilityCommand(String name, String city, String zipCode, String street, String buildingNumber, List<UpsertDoctorCommand> upsertDoctorCommands) {
        return InsertFacilityCommand.builder()
                .name(name)
                .city(city)
                .zipCode(zipCode)
                .street(street)
                .buildingNumber(buildingNumber)
                .doctors(upsertDoctorCommands)
                .build();
    }

    private UpdateFacilityCommand buildUpdateFacilityCommand() {
        return UpdateFacilityCommand.builder()
                .name("name")
                .city("city")
                .zipCode("zipCode")
                .street("street")
                .buildingNumber("buildingNumber")
                .build();
    }

    private UpdateFacilityCommand buildUpdateFacilityCommand(String name, String city, String zipCode, String street, String buildingNumber) {
        return UpdateFacilityCommand.builder()
                .name(name)
                .city(city)
                .zipCode(zipCode)
                .street(street)
                .buildingNumber(buildingNumber)
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

    private UpsertDoctorCommand buildUpsertDoctorCommand(String email, String password, String firstName, String lastName, String specialization) {
        return UpsertDoctorCommand.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .specialization(specialization)
                .build();
    }

    private Stream<Arguments> provideInsertFacilityCommandsWithNulls() {
        return Stream.of(
                Arguments.of(buildInsertFacilityCommand(null, "city", "zipCode", "street", "buildingNumber", List.of(buildUpsertDoctorCommand()))),
                Arguments.of(buildInsertFacilityCommand("name", null, "zipCode", "street", "buildingNumber", List.of(buildUpsertDoctorCommand()))),
                Arguments.of(buildInsertFacilityCommand("name", "city", null, "street", "buildingNumber", List.of(buildUpsertDoctorCommand()))),
                Arguments.of(buildInsertFacilityCommand("name", "city", "zipCode", null, "buildingNumber", List.of(buildUpsertDoctorCommand()))),
                Arguments.of(buildInsertFacilityCommand("name", "city", "zipCode", "street", null, List.of(buildUpsertDoctorCommand()))),
                Arguments.of(buildInsertFacilityCommand("name", "city", "zipCode", "street", "buildingNumber", null))
        );
    }

    private Stream<Arguments> provideUpdateFacilityCommandsWithNulls() {
        return Stream.of(
                Arguments.of(buildUpdateFacilityCommand(null, "city", "zipCode", "street", "buildingNumber")),
                Arguments.of(buildUpdateFacilityCommand("name", null, "zipCode", "street", "buildingNumber")),
                Arguments.of(buildUpdateFacilityCommand("name", "city", null, "street", "buildingNumber")),
                Arguments.of(buildUpdateFacilityCommand("name", "city", "zipCode", null, "buildingNumber")),
                Arguments.of(buildUpdateFacilityCommand("name", "city", "zipCode", "street", null))
        );
    }

    private Stream<UpsertDoctorCommand> streamUpsertDoctorCommandsWithNulls() {
        return Stream.of(
                buildUpsertDoctorCommand(null, "password", "firstName", "lastName", "specialization"),
                buildUpsertDoctorCommand("email", null, "firstName", "lastName", "specialization"),
                buildUpsertDoctorCommand("email", "password", null, "lastName", "specialization"),
                buildUpsertDoctorCommand("email", "password", "firstName", null, "specialization"),
                buildUpsertDoctorCommand("email", "password", "firstName", "lastName", null)
        );
    }

    private Stream<Arguments> provideUpsertFacilityCommandsUpsertDoctorCommandsWithNulls() {
        Stream.Builder<Arguments> argStream = Stream.builder();
        streamUpsertDoctorCommandsWithNulls().forEach(upsertDoctorCommand -> {
            InsertFacilityCommand insertFacilityCommand = buildInsertFacilityCommand("name", "city", "zipCode", "street", "buildingNumber", List.of(upsertDoctorCommand));
            argStream.add(Arguments.of(insertFacilityCommand));
        });
        return argStream.build();
    }

    private String getDefaultErrorTimeString() {
        return "2012-12-12T12:00Z";
    }
}

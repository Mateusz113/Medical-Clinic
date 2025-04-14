package com.example.demo.service;

import com.example.demo.argument_matcher.VisitArgumentMatcher;
import com.example.demo.command.visit.InsertVisitCommand;
import com.example.demo.exception.doctor.DoctorNotFoundException;
import com.example.demo.exception.patient.PatientNotFoundException;
import com.example.demo.exception.visit.VisitIllegalDataException;
import com.example.demo.exception.visit.VisitNotAvailableException;
import com.example.demo.exception.visit.VisitNotFoundException;
import com.example.demo.mapper.VisitMapper;
import com.example.demo.model.PageableContentDto;
import com.example.demo.model.doctor.Doctor;
import com.example.demo.model.doctor.SimpleDoctorDTO;
import com.example.demo.model.patient.Patient;
import com.example.demo.model.patient.PatientDTO;
import com.example.demo.model.visit.Visit;
import com.example.demo.model.visit.VisitDTO;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VisitServiceTest {
    private VisitRepository visitRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private VisitService visitService;

    @BeforeEach
    void setUp() {
        visitRepository = mock(VisitRepository.class);
        doctorRepository = mock(DoctorRepository.class);
        patientRepository = mock(PatientRepository.class);
        VisitMapper visitMapper = Mappers.getMapper(VisitMapper.class);
        Clock clock = Clock.fixed(Instant.parse("2012-12-12T12:00:00Z"), ZoneOffset.UTC);
        visitService = new VisitService(visitRepository, visitMapper, doctorRepository, patientRepository, clock);
    }

    @ParameterizedTest
    @MethodSource("provideInsertVisitCommandsWithNulls")
    public void createVisit_VisitDataHasNullValues_ThrowsVisitIllegalDataException(InsertVisitCommand insertVisitCommand) {
        //given
        //when
        VisitIllegalDataException exception = assertThrows(VisitIllegalDataException.class, () -> visitService.createVisit(insertVisitCommand));
        //then
        assertEquals("There cannot be nulls in visit data.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(getDefaultTimeString(), exception.getDate().toString());
    }

    @Test
    public void createVisit_DoctorWithIdDoesNotExist_ThrowsDoctorNotFoundException() {
        //given
        InsertVisitCommand insertVisitCommand = buildInsertVisitCommand();
        when(doctorRepository.findById(insertVisitCommand.doctorId())).thenReturn(Optional.empty());
        //when
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class, () -> visitService.createVisit(insertVisitCommand));
        //then
        assertEquals("Doctor with id: %d does not exist.".formatted(insertVisitCommand.doctorId()), exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(getDefaultTimeString(), exception.getDate().toString());
    }

    @Test
    public void createVisit_StartDateIsSetInPast_ThrowsVisitIllegalDataException() {
        //given
        InsertVisitCommand insertVisitCommand = getInsertVisitCommand(getOffsetDateTime(Instant.parse("2012-12-10T12:00:00Z")), getOffsetDateTime(Instant.parse("2012-12-13T12:00:00Z")), 1L);
        Doctor doctor = buildDoctor();
        when(doctorRepository.findById(insertVisitCommand.doctorId())).thenReturn(Optional.of(doctor));
        //when
        VisitIllegalDataException exception = assertThrows(VisitIllegalDataException.class, () -> visitService.createVisit(insertVisitCommand));
        //then
        assertEquals("The visit cannot be set in the past.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(getDefaultTimeString(), exception.getDate().toString());
    }

    @Test
    public void createVisit_EndTimeIsBeforeTheStartTime_ThrowsVisitIllegalDataException() {
        //given
        InsertVisitCommand insertVisitCommand = getInsertVisitCommand(getOffsetDateTime(Instant.parse("2012-12-13T12:00:00Z")), getOffsetDateTime(Instant.parse("2012-12-13T11:00:00Z")), 1L);
        Doctor doctor = buildDoctor();
        when(doctorRepository.findById(insertVisitCommand.doctorId())).thenReturn(Optional.of(doctor));
        //when
        VisitIllegalDataException exception = assertThrows(VisitIllegalDataException.class, () -> visitService.createVisit(insertVisitCommand));
        //then
        assertEquals("The visit end date has to be later than start date.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(getDefaultTimeString(), exception.getDate().toString());
    }

    @Test
    public void createVisit_EndTimeIsEqualToTheStartTime_ThrowsVisitIllegalDataException() {
        //given
        InsertVisitCommand insertVisitCommand = getInsertVisitCommand(getOffsetDateTime(Instant.parse("2012-12-13T12:00:00Z")), getOffsetDateTime(Instant.parse("2012-12-13T12:00:00Z")), 1L);
        Doctor doctor = buildDoctor();
        when(doctorRepository.findById(insertVisitCommand.doctorId())).thenReturn(Optional.of(doctor));
        //when
        VisitIllegalDataException exception = assertThrows(VisitIllegalDataException.class, () -> visitService.createVisit(insertVisitCommand));
        //then
        assertEquals("The visit end date has to be later than start date.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(getDefaultTimeString(), exception.getDate().toString());
    }

    @Test
    public void createVisit_StartTimeMinutesDoNotDivideBy15_ThrowsVisitIllegalDataException() {
        //given
        InsertVisitCommand insertVisitCommand = getInsertVisitCommand(getOffsetDateTime(Instant.parse("2012-12-13T12:02:00Z")), getOffsetDateTime(Instant.parse("2012-12-13T13:00:00Z")), 1L);
        Doctor doctor = buildDoctor();
        when(doctorRepository.findById(insertVisitCommand.doctorId())).thenReturn(Optional.of(doctor));
        //when
        VisitIllegalDataException exception = assertThrows(VisitIllegalDataException.class, () -> visitService.createVisit(insertVisitCommand));
        //then
        assertEquals("The visit time must be set to a full quarter-hour increment e.g. 13:15.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(getDefaultTimeString(), exception.getDate().toString());
    }

    @Test
    public void createVisit_EndTimeMinutesDoNotDivideBy15_ThrowsVisitIllegalDataException() {
        //given
        InsertVisitCommand insertVisitCommand = getInsertVisitCommand(getOffsetDateTime(Instant.parse("2012-12-13T12:00:00Z")), getOffsetDateTime(Instant.parse("2012-12-13T13:01:00Z")), 1L);
        Doctor doctor = buildDoctor();
        when(doctorRepository.findById(insertVisitCommand.doctorId())).thenReturn(Optional.of(doctor));
        //when
        VisitIllegalDataException exception = assertThrows(VisitIllegalDataException.class, () -> visitService.createVisit(insertVisitCommand));
        //then
        assertEquals("The visit time must be set to a full quarter-hour increment e.g. 13:15.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(getDefaultTimeString(), exception.getDate().toString());
    }

    @Test
    public void createVisit_VisitIsAlreadyScheduledAtThatTime_ThrowsVisitIllegalDataException() {
        //given
        InsertVisitCommand insertVisitCommand = getInsertVisitCommand(getOffsetDateTime(Instant.parse("2012-12-13T12:00:00Z")), getOffsetDateTime(Instant.parse("2012-12-13T13:00:00Z")), 1L);
        Doctor doctor = buildDoctor();
        when(doctorRepository.findById(insertVisitCommand.doctorId())).thenReturn(Optional.of(doctor));
        when(visitRepository.existsBetweenDatesInclusive(insertVisitCommand.startTime(), insertVisitCommand.endTime(), doctor)).thenReturn(true);
        //when
        VisitIllegalDataException exception = assertThrows(VisitIllegalDataException.class, () -> visitService.createVisit(insertVisitCommand));
        //then
        assertEquals("There is a visit already scheduled at that time.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(getDefaultTimeString(), exception.getDate().toString());
    }

    @Test
    public void createVisit_DataIsCorrect_SavesVisit() {
        //given
        InsertVisitCommand insertVisitCommand = getInsertVisitCommand(getOffsetDateTime(Instant.parse("2012-12-13T12:00:00Z")), getOffsetDateTime(Instant.parse("2012-12-13T13:00:00Z")), 1L);
        Doctor doctor = buildDoctor();
        SimpleDoctorDTO expectedDoctor = new SimpleDoctorDTO(doctor.getId(), doctor.getEmail(), doctor.getFirstName(), doctor.getLastName(), doctor.getSpecialization());
        Visit expectedSavedVisit = Visit.builder()
                .id(null)
                .startTime(insertVisitCommand.startTime())
                .endTime(insertVisitCommand.endTime())
                .patient(null)
                .doctor(doctor)
                .build();
        ArgumentMatcher<Visit> visitMatcher = new VisitArgumentMatcher(expectedSavedVisit);
        when(doctorRepository.findById(insertVisitCommand.doctorId())).thenReturn(Optional.of(doctor));
        when(visitRepository.existsBetweenDatesInclusive(insertVisitCommand.startTime(), insertVisitCommand.endTime(), doctor)).thenReturn(false);
        when(visitRepository.save(argThat(visitMatcher))).thenReturn(expectedSavedVisit);
        //when
        VisitDTO result = visitService.createVisit(insertVisitCommand);
        //then
        assertNull(result.id());
        assertEquals(getOffsetDateTime(Instant.parse("2012-12-13T12:00:00Z")), result.startTime());
        assertEquals(getOffsetDateTime(Instant.parse("2012-12-13T13:00:00Z")), result.endTime());
        assertNull(result.patient());
        assertEquals(expectedDoctor, result.doctor());
    }


    @Test
    public void getAllVisits_ThereAreNoVisits_ReturnsEmptyPageableContentDto() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        when(visitRepository.findAll(pageable)).thenReturn(Page.empty());
        //when
        PageableContentDto<VisitDTO> result = visitService.getAllVisits(pageable);
        //then
        assertEquals(0, result.totalEntries());
        assertEquals(1, result.totalNumberOfPages());
        assertEquals(0, result.pageNumber());
        assertEquals(0, result.content().size());
    }

    @Test
    public void getAllVisits_ThereAreVisits_ReturnsCorrectPageableContentDto() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        Visit visit1 = buildVisit();
        Visit visit2 = buildVisit();
        visit2.setId(2L);
        Page<Visit> page = new PageImpl<>(List.of(visit1, visit2), pageable, 2);
        SimpleDoctorDTO expectedDoctor = buildSimpleDoctor();
        PatientDTO expectedPatient = buildPatientDto();
        when(visitRepository.findAll(pageable)).thenReturn(page);
        //when
        PageableContentDto<VisitDTO> result = visitService.getAllVisits(pageable);
        //then
        assertEquals(2, result.totalEntries());
        assertEquals(1, result.totalNumberOfPages());
        assertEquals(0, result.pageNumber());
        assertEquals(2, result.content().size());
        assertEquals(1L, result.content().getFirst().id());
        assertEquals(getOffsetDateTime(Instant.parse("2012-12-13T12:00:00Z")), result.content().getFirst().startTime());
        assertEquals(getOffsetDateTime(Instant.parse("2012-12-13T13:00:00Z")), result.content().getFirst().endTime());
        assertEquals(expectedDoctor, result.content().getFirst().doctor());
        assertNull(result.content().getFirst().patient());
        assertEquals(2L, result.content().get(1).id());
        assertEquals(getOffsetDateTime(Instant.parse("2012-12-13T12:00:00Z")), result.content().get(1).startTime());
        assertEquals(getOffsetDateTime(Instant.parse("2012-12-13T13:00:00Z")), result.content().get(1).endTime());
        assertEquals(expectedDoctor, result.content().get(1).doctor());
        assertNull(result.content().get(1).patient());
    }

    @Test
    public void getDoctorVisits_ThereAreNoVisits_ReturnsEmptyPageableContentDto() {
        //given
        Long doctorId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        when(visitRepository.findAllByDoctorId(doctorId, pageable)).thenReturn(Page.empty());
        //when
        PageableContentDto<VisitDTO> result = visitService.getDoctorVisits(doctorId, pageable, false);
        //then
        assertEquals(0, result.totalEntries());
        assertEquals(1, result.totalNumberOfPages());
        assertEquals(0, result.pageNumber());
        assertEquals(0, result.content().size());
    }

    @Test
    public void getDoctorVisits_ThereAreVisits_ReturnsCorrectPageableContentDto() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        long doctorId = 1;
        Visit visit1 = buildVisit();
        Visit visit2 = buildVisit();
        visit2.setId(2L);
        Page<Visit> page = new PageImpl<>(List.of(visit1, visit2), pageable, 2);
        SimpleDoctorDTO expectedDoctor = buildSimpleDoctor();
        PatientDTO expectedPatient = buildPatientDto();
        when(visitRepository.findAllByDoctorId(doctorId, pageable)).thenReturn(page);
        //when
        PageableContentDto<VisitDTO> result = visitService.getDoctorVisits(doctorId, pageable, false);
        //then
        assertEquals(2, result.totalEntries());
        assertEquals(1, result.totalNumberOfPages());
        assertEquals(0, result.pageNumber());
        assertEquals(2, result.content().size());
        assertEquals(1L, result.content().getFirst().id());
        assertEquals(getOffsetDateTime(Instant.parse("2012-12-13T12:00:00Z")), result.content().getFirst().startTime());
        assertEquals(getOffsetDateTime(Instant.parse("2012-12-13T13:00:00Z")), result.content().getFirst().endTime());
        assertEquals(expectedDoctor, result.content().getFirst().doctor());
        assertNull(result.content().getFirst().patient());
        assertEquals(2L, result.content().get(1).id());
        assertEquals(getOffsetDateTime(Instant.parse("2012-12-13T12:00:00Z")), result.content().get(1).startTime());
        assertEquals(getOffsetDateTime(Instant.parse("2012-12-13T13:00:00Z")), result.content().get(1).endTime());
        assertEquals(expectedDoctor, result.content().get(1).doctor());
        assertNull(result.content().get(1).patient());
    }

    @Test
    public void getPatientVisits_ThereAreNoVisits_ReturnsEmptyPageableContentDto() {
        //given
        Long patientId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        when(visitRepository.findAllByPatientId(patientId, pageable)).thenReturn(Page.empty());
        //when
        PageableContentDto<VisitDTO> result = visitService.getPatientVisits(patientId, pageable);
        //then
        assertEquals(0, result.totalEntries());
        assertEquals(1, result.totalNumberOfPages());
        assertEquals(0, result.pageNumber());
        assertEquals(0, result.content().size());
    }

    @Test
    public void getPatientVisits_ThereAreVisits_ReturnsCorrectPageableContentDto() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        long patientId = 1;
        Visit visit1 = buildVisit();
        Visit visit2 = buildVisit();
        visit2.setId(2L);
        Page<Visit> page = new PageImpl<>(List.of(visit1, visit2), pageable, 2);
        SimpleDoctorDTO expectedDoctor = buildSimpleDoctor();
        PatientDTO expectedPatient = buildPatientDto();
        when(visitRepository.findAllByPatientId(patientId, pageable)).thenReturn(page);
        //when
        PageableContentDto<VisitDTO> result = visitService.getPatientVisits(patientId, pageable);
        //then
        assertEquals(2, result.totalEntries());
        assertEquals(1, result.totalNumberOfPages());
        assertEquals(0, result.pageNumber());
        assertEquals(2, result.content().size());
        assertEquals(1L, result.content().getFirst().id());
        assertEquals(getOffsetDateTime(Instant.parse("2012-12-13T12:00:00Z")), result.content().getFirst().startTime());
        assertEquals(getOffsetDateTime(Instant.parse("2012-12-13T13:00:00Z")), result.content().getFirst().endTime());
        assertEquals(expectedDoctor, result.content().getFirst().doctor());
        assertNull(result.content().getFirst().patient());
        assertEquals(2L, result.content().get(1).id());
        assertEquals(getOffsetDateTime(Instant.parse("2012-12-13T12:00:00Z")), result.content().get(1).startTime());
        assertEquals(getOffsetDateTime(Instant.parse("2012-12-13T13:00:00Z")), result.content().get(1).endTime());
        assertEquals(expectedDoctor, result.content().get(1).doctor());
        assertNull(result.content().get(1).patient());
    }

    @Test
    public void registerPatientToVisit_VisitDoesNotExist_ThrowsVisitNotFoundException() {
        //given
        long visitId = 1;
        long patientId = 1;
        when(visitRepository.findById(visitId)).thenReturn(Optional.empty());
        //when
        VisitNotFoundException exception = assertThrows(VisitNotFoundException.class, () -> visitService.registerPatientToVisit(visitId, patientId));
        //then
        assertEquals("Visit with id: %d does not exist.".formatted(visitId), exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(getDefaultTimeString(), exception.getDate().toString());
    }

    @Test
    public void registerPatientToVisit_PatientDoesNotExist_ThrowsPatientNotFoundException() {
        //given
        long visitId = 1;
        long patientId = 1;
        Visit visit = buildVisit();
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());
        //when
        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class, () -> visitService.registerPatientToVisit(visitId, patientId));
        //then
        assertEquals("Patient with id: %d does not exist.".formatted(patientId), exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(getDefaultTimeString(), exception.getDate().toString());
    }

    @Test
    public void registerPatientToVisit_VisitStartTimeIsInThePast_ThrowsVisitNotAvailableException() {
        //given
        long visitId = 1;
        long patientId = 1;
        Visit visit = buildVisit();
        Patient patient = buildPatient();
        visit.setStartTime(visit.getStartTime().minusMonths(3));
        visit.setEndTime(visit.getEndTime().minusMonths(3));
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        //when
        VisitNotAvailableException exception = assertThrows(VisitNotAvailableException.class, () -> visitService.registerPatientToVisit(visitId, patientId));
        //then
        assertEquals("Patient cannot register to the past visits.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(getDefaultTimeString(), exception.getDate().toString());
    }

    @Test
    public void registerPatientToVisit_VisitIsBooked_ThrowsVisitNotAvailableException() {
        //given
        long visitId = 1;
        long patientId = 1;
        Visit visit = buildVisit();
        Patient patient = buildPatient();
        Patient anotherPatient = buildPatient();
        anotherPatient.setId(2L);
        visit.setPatient(anotherPatient);
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        //when
        VisitNotAvailableException exception = assertThrows(VisitNotAvailableException.class, () -> visitService.registerPatientToVisit(visitId, patientId));
        //then
        assertEquals("Patient is already registered to that visit.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(getDefaultTimeString(), exception.getDate().toString());
    }

    @Test
    public void registerPatientToVisit_VisitIsAvailable_AssignsPatientToVisit() {
        //given
        long visitId = 1;
        long patientId = 1;
        Visit visit = buildVisit();
        Patient patient = buildPatient();
        Visit expectedVisit = buildVisit();
        expectedVisit.setPatient(patient);
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        //when
        visitService.registerPatientToVisit(visitId, patientId);
        //then
        verify(visitRepository, times(1)).save(argThat(new VisitArgumentMatcher(expectedVisit)));
    }

    @Test
    public void deleteVisit_VisitDoesNotExist_ThrowsVisitNotFoundException() {
        //given
        long visitId = 1;
        when(visitRepository.findById(visitId)).thenReturn(Optional.empty());
        //when
        VisitNotFoundException exception = assertThrows(VisitNotFoundException.class, () -> visitService.deleteVisit(visitId));
        //then
        assertEquals("Visit with id: %d does not exist.".formatted(visitId), exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(getDefaultTimeString(), exception.getDate().toString());
    }

    @Test
    public void deleteVisit_VisitExists_DeletesVisit() {
        //given
        long visitId = 1;
        Visit visit = buildVisit();
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        //when
        visitService.deleteVisit(visitId);
        //then
        verify(visitRepository, times(1)).delete(argThat(new VisitArgumentMatcher(visit)));
    }

    private OffsetDateTime getOffsetDateTime(Instant instant) {
        return OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    private InsertVisitCommand buildInsertVisitCommand() {
        return InsertVisitCommand.builder()
                .startTime(getOffsetDateTime(Instant.parse("2012-12-13T12:00:00Z")))
                .endTime(getOffsetDateTime(Instant.parse("2012-12-13T12:30:00Z")))
                .doctorId(1L)
                .build();
    }

    private Visit buildVisit() {
        return Visit.builder()
                .id(1L)
                .startTime(getOffsetDateTime(Instant.parse("2012-12-13T12:00:00Z")))
                .endTime(getOffsetDateTime(Instant.parse("2012-12-13T13:00:00Z")))
                .patient(null)
                .doctor(buildDoctor())
                .build();
    }

    private InsertVisitCommand getInsertVisitCommand(OffsetDateTime startTime, OffsetDateTime endTime, Long doctorId) {
        return InsertVisitCommand.builder()
                .startTime(startTime)
                .endTime(endTime)
                .doctorId(doctorId)
                .build();
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

    private SimpleDoctorDTO buildSimpleDoctor() {
        return new SimpleDoctorDTO(1L, "email", "firstName", "lastName", "specialization");
    }

    private Patient buildPatient() {
        return Patient.builder()
                .id(1L)
                .email("email")
                .password("password")
                .idCardNo("idCardNo")
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("phoneNumber")
                .birthday(LocalDate.of(2012, 12, 12))
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

    private String getDefaultTimeString() {
        return "2012-12-12T12:00Z";
    }

    private Stream<Arguments> provideInsertVisitCommandsWithNulls() {
        return Stream.of(
                Arguments.of(getInsertVisitCommand(null, getOffsetDateTime(Instant.parse("2012-12-13T12:00:00Z")), 1L)),
                Arguments.of(getInsertVisitCommand(getOffsetDateTime(Instant.parse("2012-12-13T12:00:00Z")), null, 1L)),
                Arguments.of(getInsertVisitCommand(getOffsetDateTime(Instant.parse("2012-12-13T12:00:00Z")), getOffsetDateTime(Instant.parse("2012-12-13T12:00:00Z")), null))
        );
    }
}
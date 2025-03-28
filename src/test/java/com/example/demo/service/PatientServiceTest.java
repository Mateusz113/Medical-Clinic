package com.example.demo.service;

import com.example.demo.command.patient.UpsertPatientCommand;
import com.example.demo.exception.patient.PatientAlreadyExistsException;
import com.example.demo.exception.patient.PatientIllegalDataException;
import com.example.demo.exception.patient.PatientNotFoundException;
import com.example.demo.mapper.PatientMapper;
import com.example.demo.model.PageableContentDto;
import com.example.demo.model.patient.Patient;
import com.example.demo.model.patient.PatientDTO;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.VisitRepository;
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
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PatientServiceTest {
    private PatientRepository patientRepository;
    private PatientService patientService;

    @BeforeEach
    void setUp() {
        patientRepository = mock(PatientRepository.class);
        VisitRepository visitRepository = mock(VisitRepository.class);
        PatientMapper patientMapper = Mappers.getMapper(PatientMapper.class);
        Clock clock = Clock.fixed(Instant.parse("2012-12-12T12:00:00Z"), ZoneOffset.UTC);
        patientService = new PatientService(patientRepository, patientMapper, visitRepository, clock);
    }

    @ParameterizedTest
    @MethodSource("provideUpsertPatientCommandsWithNulls")
    void createPatient_PatientDataHasNullValues_ThrowsPatientIllegalDataException(UpsertPatientCommand upsertPatientCommand) {
        //given
        //when
        PatientIllegalDataException exception = assertThrows(PatientIllegalDataException.class, () -> patientService.createPatient(upsertPatientCommand));
        //then
        assertEquals("There cannot be null fields in patient data.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("2012-12-12T12:00Z", exception.getDate().toString());
    }

    @Test
    void createPatient_PatientWithEmailAlreadyExists_ThrowsPatientAlreadyExistsException() {
        //given
        UpsertPatientCommand upsertPatientCommand = buildUpsertPatientCommand();
        when(patientRepository.existsByEmail(upsertPatientCommand.email())).thenReturn(true);
        //when
        PatientAlreadyExistsException exception = assertThrows(PatientAlreadyExistsException.class, () -> patientService.createPatient(upsertPatientCommand));
        //then
        assertEquals("Patient with email: %s already exists.".formatted(upsertPatientCommand.email()), exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("2012-12-12T12:00Z", exception.getDate().toString());
    }

    @Test
    void createPatient_PatientDataIsCorrect_ReturnsPatientDTO() {
        //given
        UpsertPatientCommand upsertPatientCommand = buildUpsertPatientCommand();
        Patient patient = buildPatient();
        PatientDTO expected = PatientDTO.builder()
                .id(1L)
                .email("email")
                .idCardNo("idCardNo")
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("phoneNumber")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();
        when(patientRepository.existsByEmail(upsertPatientCommand.email())).thenReturn(false);
        when(patientRepository.save(patient)).thenReturn(patient);
        //when
        PatientDTO result = patientService.createPatient(upsertPatientCommand);
        //then
        assertEquals(expected.id(), result.id());
        assertEquals(expected.email(), result.email());
        assertEquals(expected.idCardNo(), result.idCardNo());
        assertEquals(expected.firstName(), result.firstName());
        assertEquals(expected.lastName(), result.lastName());
        assertEquals(expected.phoneNumber(), result.phoneNumber());
        assertEquals(expected.birthday(), result.birthday());
    }

    @Test
    void getAllPatients_ThereAreNoPatients_ReturnsEmptyPageableContentDto() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        when(patientRepository.findAll(pageable)).thenReturn(Page.empty());
        //when
        PageableContentDto<PatientDTO> result = patientService.getAllPatients(pageable);
        //then
        assertEquals(0, result.pageNumber());
        assertEquals(0, result.totalEntries());
        assertEquals(1, result.totalNumberOfPages());
        assertTrue(result.content().isEmpty());
    }

    @Test
    void getAllPatients_ThereArePatients_ReturnsCorrectPageableContentDto() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        Patient patient1 = buildPatient();
        Patient patient2 = buildPatient(2L);
        List<Patient> patients = List.of(patient1, patient2);
        Page<Patient> patientPage = new PageImpl<>(patients, pageable, 2);
        when(patientRepository.findAll(pageable)).thenReturn(patientPage);
        //when
        PageableContentDto<PatientDTO> result = patientService.getAllPatients(pageable);
        //then
        assertEquals(0, result.pageNumber());
        assertEquals(2, result.totalEntries());
        assertEquals(1, result.totalNumberOfPages());
        assertEquals(2, result.content().size());
        assertEquals(1L, result.content().getFirst().id());
        assertEquals("email", result.content().getFirst().email());
        assertEquals("idCardNo", result.content().getFirst().idCardNo());
        assertEquals("firstName", result.content().getFirst().firstName());
        assertEquals("lastName", result.content().getFirst().lastName());
        assertEquals("phoneNumber", result.content().getFirst().phoneNumber());
        assertEquals(2L, result.content().get(1).id());
        assertEquals("email", result.content().get(1).email());
        assertEquals("idCardNo", result.content().get(1).idCardNo());
        assertEquals("firstName", result.content().get(1).firstName());
        assertEquals("lastName", result.content().get(1).lastName());
        assertEquals("phoneNumber", result.content().get(1).phoneNumber());
        assertEquals(LocalDate.of(2012, 12, 12), result.content().get(1).birthday());
    }

    @Test
    void getPatient_PatientDoesNotExist_ThrowsPatientNotFoundException() {
        //given
        String email = "email";
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());
        //when
        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class, () -> patientService.getPatient(email));
        //then
        assertEquals("Patient with email: %s does not exist.".formatted(email), exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("2012-12-12T12:00Z", exception.getDate().toString());
    }

    @Test
    void getPatient_PatientExists_ReturnsPatientDTO() {
        //given
        String email = "email";
        Patient currentPatient = buildPatient();
        PatientDTO expected = PatientDTO.builder()
                .id(1L)
                .email("email")
                .idCardNo("idCardNo")
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("phoneNumber")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(currentPatient));
        //when
        PatientDTO result = patientService.getPatient(email);
        //then
        assertEquals(expected.id(), result.id());
        assertEquals(expected.email(), result.email());
        assertEquals(expected.idCardNo(), result.idCardNo());
        assertEquals(expected.firstName(), result.firstName());
        assertEquals(expected.lastName(), result.lastName());
        assertEquals(expected.phoneNumber(), result.phoneNumber());
        assertEquals(expected.birthday(), result.birthday());
    }

    @ParameterizedTest
    @MethodSource("provideUpsertPatientCommandsWithNulls")
    void editPatient_PatientDataHasNullValues_ThrowsPatientIllegalDataException(UpsertPatientCommand upsertPatientCommand) {
        //given
        String email = "email";
        Patient currentPatient = buildPatient();
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(currentPatient));
        //when
        PatientIllegalDataException exception = assertThrows(PatientIllegalDataException.class, () -> patientService.editPatient(email, upsertPatientCommand));
        //then
        assertEquals("There cannot be null fields in patient data.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("2012-12-12T12:00Z", exception.getDate().toString());
    }

    @Test
    void editPatient_PatientDataContainsEmailThatExistsAndIsDifferentFromCurrentEmail_ThrowsPatientAlreadyExistsException() {
        //given
        String email = "initial email";
        UpsertPatientCommand upsertPatientCommand = buildUpsertPatientCommand();
        Patient currentPatient = buildPatient();
        currentPatient.setEmail(email);
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(currentPatient));
        when(patientRepository.existsByEmail(upsertPatientCommand.email())).thenReturn(true);
        //when
        PatientAlreadyExistsException exception = assertThrows(PatientAlreadyExistsException.class, () -> patientService.editPatient(email, upsertPatientCommand));
        //then
        assertEquals("Patient with email: %s already exists.".formatted(upsertPatientCommand.email()), exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("2012-12-12T12:00Z", exception.getDate().toString());
    }

    @Test
    void editPatient_PatientDataContainsNewIdCardNo_ThrowsPatientIllegalDataException() {
        //given
        String email = "initial email";
        UpsertPatientCommand upsertPatientCommand = getUpsertPatientCommand("email", "password", "new idCardNo", "firstName", "lastName", "phoneNumber", LocalDate.of(2012, 12, 12));
        Patient currentPatient = buildPatient();
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(currentPatient));
        when(patientRepository.existsByEmail(upsertPatientCommand.email())).thenReturn(false);
        //when
        PatientIllegalDataException exception = assertThrows(PatientIllegalDataException.class, () -> patientService.editPatient(email, upsertPatientCommand));
        //then
        assertEquals("ID card number cannot be changed.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("2012-12-12T12:00Z", exception.getDate().toString());
    }

    @Test
    void editPatient_PatientDataIsCorrect_EditsPatient() {
        //given
        String email = "email";
        UpsertPatientCommand upsertPatientCommand = UpsertPatientCommand.builder()
                .email("new email")
                .password("new password")
                .idCardNo("idCardNo")
                .firstName("new firstName")
                .lastName("new lastName")
                .phoneNumber("new phoneNumber")
                .birthday(LocalDate.of(2012, 11, 11))
                .build();
        Patient currentPatient = buildPatient();
        PatientDTO expected = PatientDTO.builder()
                .id(1L)
                .email("new email")
                .idCardNo("idCardNo")
                .firstName("new firstName")
                .lastName("new lastName")
                .phoneNumber("new phoneNumber")
                .birthday(LocalDate.of(2012, 11, 11))
                .build();
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(currentPatient));
        when(patientRepository.existsByEmail(upsertPatientCommand.email())).thenReturn(false);
        //when
        PatientDTO result = patientService.editPatient(email, upsertPatientCommand);
        //then
        assertEquals(expected.id(), result.id());
        assertEquals(expected.email(), result.email());
        assertEquals(expected.idCardNo(), result.idCardNo());
        assertEquals(expected.firstName(), result.firstName());
        assertEquals(expected.lastName(), result.lastName());
        assertEquals(expected.phoneNumber(), result.phoneNumber());
        assertEquals(expected.birthday(), result.birthday());
    }

    @Test
    void editPatientPassword_NewPasswordIsNull_ThrowsPatientIllegalDataException() {
        //given
        String email = "email";
        String password = null;
        //when
        PatientIllegalDataException exception = assertThrows(PatientIllegalDataException.class, () -> patientService.editPatientPassword(email, password));
        //then
        assertEquals("Password cannot be set to null.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("2012-12-12T12:00Z", exception.getDate().toString());
    }

    @Test
    void editPatientPassword_PatientDoesNotExist_ThrowsPatientNotFoundException() {
        //given
        String email = "email";
        String password = "password";
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());
        //when
        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class, () -> patientService.editPatientPassword(email, password));
        //then
        assertEquals("Patient with email: %s does not exist.".formatted(email), exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("2012-12-12T12:00Z", exception.getDate().toString());
    }

    @Test
    void editPatientPassword_NewPasswordIsCorrect_EditsPatientPassword() {
        //given
        String email = "email";
        String password = "different password";
        Patient currentPatient = buildPatient();
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(currentPatient));
        //when
        patientService.editPatientPassword(email, password);
        //then
        assertEquals(password, currentPatient.getPassword());
    }

    @Test
    void deletePatient_PatientWithEmailDoesNotExist_ThrowsPatientNotFoundException() {
        //given
        String email = "email";
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());
        //when
        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class, () -> patientService.deletePatient(email));
        //then
        assertEquals("Patient with email: %s does not exist.".formatted(email), exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("2012-12-12T12:00Z", exception.getDate().toString());
    }

    @Test
    void deletePatient_PatientWithEmailExists_DeletesPatient() {
        //given
        String email = "email";
        Patient currentPatient = buildPatient();
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(currentPatient));
        //when
        patientService.deletePatient(email);
        //then
        verify(patientRepository, times(1)).delete(currentPatient);
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

    private Patient buildPatient(Long id) {
        Patient patient = buildPatient();
        patient.setId(id);
        return patient;
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

    private UpsertPatientCommand getUpsertPatientCommand(String email, String password, String idCardNo, String firstName, String lastName, String phoneNumber, LocalDate birthday) {
        return UpsertPatientCommand.builder()
                .email(email)
                .password(password)
                .idCardNo(idCardNo)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .birthday(birthday)
                .build();
    }

    private Stream<Arguments> provideUpsertPatientCommandsWithNulls() {
        return Stream.of(
                Arguments.of(getUpsertPatientCommand(null, "password", "idCardNo", "firstName", "lastName", "phoneNumber", LocalDate.of(2012, 12, 12))),
                Arguments.of(getUpsertPatientCommand("email", null, "idCardNo", "firstName", "lastName", "phoneNumber", LocalDate.of(2012, 12, 12))),
                Arguments.of(getUpsertPatientCommand("email", "password", null, "firstName", "lastName", "phoneNumber", LocalDate.of(2012, 12, 12))),
                Arguments.of(getUpsertPatientCommand("email", "password", "idCardNo", null, "lastName", "phoneNumber", LocalDate.of(2012, 12, 12))),
                Arguments.of(getUpsertPatientCommand("email", "password", "idCardNo", "firstName", null, "phoneNumber", LocalDate.of(2012, 12, 12))),
                Arguments.of(getUpsertPatientCommand("email", "password", "idCardNo", "firstName", "lastName", null, LocalDate.of(2012, 12, 12))),
                Arguments.of(getUpsertPatientCommand("email", "password", "idCardNo", "firstName", "lastName", "phoneNumber", null))
        );
    }
}

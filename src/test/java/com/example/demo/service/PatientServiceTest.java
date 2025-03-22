package com.example.demo.service;

import com.example.demo.exception.patient.PatientAlreadyExistsException;
import com.example.demo.exception.patient.PatientIllegalDataException;
import com.example.demo.exception.patient.PatientNotFoundException;
import com.example.demo.mapper.PatientMapper;
import com.example.demo.model.PageableContentDto;
import com.example.demo.model.patient.FullPatientDataDTO;
import com.example.demo.model.patient.Patient;
import com.example.demo.model.patient.PatientDTO;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PatientServiceTest {
    private PatientRepository patientRepository;
    private PatientService patientService;

    @BeforeEach
    void setUp() {
        patientRepository = mock(PatientRepository.class);
        PatientMapper patientMapper = Mappers.getMapper(PatientMapper.class);
        VisitRepository visitRepository = mock(VisitRepository.class);
        patientService = new PatientService(patientRepository, patientMapper, visitRepository);
    }

    @Test
    void getAllPatients_ThereAreNoPatients_ReturnsEmptyPageableContentDto() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        when(patientRepository.findAll(pageable)).thenReturn(Page.empty());
        //when
        PageableContentDto<PatientDTO> pageableContentDto = patientService.getAllPatients(pageable);
        //then
        assertEquals(0, pageableContentDto.pageNumber());
        assertEquals(0, pageableContentDto.totalEntries());
        assertEquals(1, pageableContentDto.totalNumberOfPages());
        assertTrue(pageableContentDto.content().isEmpty());
    }

    @Test
    void getAllPatients_ThereArePatients_ReturnsCorrectPageableContentDto() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        Patient currentPatient = buildPatient();
        List<Patient> patients = new ArrayList<>();
        patients.add(currentPatient);
        Page<Patient> patientPage = new PageImpl<>(patients, pageable, patients.size());
        when(patientRepository.findAll(pageable)).thenReturn(patientPage);
        //when
        PageableContentDto<PatientDTO> pageableContentDto = patientService.getAllPatients(pageable);
        //then
        assertEquals(0, pageableContentDto.pageNumber());
        assertEquals(1, pageableContentDto.totalEntries());
        assertEquals(1, pageableContentDto.totalNumberOfPages());
        assertEquals(1, pageableContentDto.content().size());
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

    @Test
    void getPatient_PatientDoesNotExist_ThrowsPatientNotFoundException() {
        //given
        String email = "email";
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());
        //when
        PatientNotFoundException result = assertThrows(PatientNotFoundException.class, () -> patientService.getPatient(email));
        //then
        assertEquals("Patient with email: %s does not exist.".formatted(email), result.getMessage());
    }

    @Test
    void createPatient_PatientDataHasNullValues_ThrowsPatientIllegalDataException() {
        //given
        FullPatientDataDTO newPatientData = FullPatientDataDTO.builder()
                .email("email")
                .password("password")
                .idCardNo("idCardNo")
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber(null)
                .birthday(null)
                .build();
        //when
        PatientIllegalDataException result = assertThrows(PatientIllegalDataException.class, () -> patientService.createPatient(newPatientData));
        //then
        assertEquals("There cannot be null fields in patient data.", result.getMessage());
    }

    @Test
    void createPatient_PatientWithEmailAlreadyExists_ThrowsPatientAlreadyExistsException() {
        //given
        FullPatientDataDTO newPatientData = FullPatientDataDTO.builder()
                .email("email")
                .password("password")
                .idCardNo("idCardNo")
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("phoneNumber")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();
        when(patientRepository.existsByEmail(newPatientData.email())).thenReturn(true);
        //when
        PatientAlreadyExistsException result = assertThrows(PatientAlreadyExistsException.class, () -> patientService.createPatient(newPatientData));
        //then
        assertEquals("Patient with email: %s already exists.".formatted(newPatientData.email()), result.getMessage());
    }

    @Test
    void createPatient_PatientDataIsCorrect_ReturnsPatientDTO() {
        //given
        FullPatientDataDTO newPatientData = FullPatientDataDTO.builder()
                .email("email")
                .password("password")
                .idCardNo("idCardNo")
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("phoneNumber")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();
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
        when(patientRepository.existsByEmail(newPatientData.email())).thenReturn(false);
        when(patientRepository.save(patient)).thenReturn(patient);
        //when
        PatientDTO result = patientService.createPatient(newPatientData);
        //then
        assertInstanceOf(PatientDTO.class, result);
        assertEquals(expected.id(), result.id());
        assertEquals(expected.email(), result.email());
        assertEquals(expected.idCardNo(), result.idCardNo());
        assertEquals(expected.firstName(), result.firstName());
        assertEquals(expected.lastName(), result.lastName());
        assertEquals(expected.phoneNumber(), result.phoneNumber());
        assertEquals(expected.birthday(), result.birthday());
    }

    @Test
    void deletePatient_PatientWithEmailDoesNotExist_ThrowsPatientNotFoundException() {
        //given
        String email = "email";
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());
        //when
        PatientNotFoundException result = assertThrows(PatientNotFoundException.class, () -> patientService.deletePatient(email));
        //then
        assertEquals("Patient with email: %s does not exist.".formatted(email), result.getMessage());
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

    @Test
    void editPatient_PatientDoesNotExist_ThrowsPatientNotFoundException() {
        //given
        String email = "email";
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());
        //when
        PatientNotFoundException result = assertThrows(PatientNotFoundException.class, () -> patientService.editPatient(email, null));
        //then
        assertEquals("Patient with email %s does not exist.".formatted(email), result.getMessage());
    }

    @Test
    void editPatient_PatientDataHasNullValues_ThrowsPatientIllegalDataException() {
        //given
        String email = "email";
        FullPatientDataDTO newPatientData = FullPatientDataDTO.builder()
                .email(null)
                .password(null)
                .idCardNo("idCardNo")
                .firstName("new firstName")
                .lastName("new lastName")
                .phoneNumber("new phoneNumber")
                .birthday(LocalDate.of(2012, 11, 11))
                .build();
        Patient currentPatient = buildPatient();
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(currentPatient));
        //when
        PatientIllegalDataException result = assertThrows(PatientIllegalDataException.class, () -> patientService.editPatient(email, newPatientData));
        //then
        assertEquals("There cannot be null fields in patient data.", result.getMessage());
    }

    @Test
    void editPatient_PatientDataContainsEmailThatExistsAndIsDifferentFromCurrentEmail_ThrowsPatientAlreadyExistsException() {
        //given
        String email = "email";
        String newEmail = "new email";
        FullPatientDataDTO newPatientData = FullPatientDataDTO.builder()
                .email(newEmail)
                .password("password")
                .idCardNo("idCardNo")
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("phoneNumber")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();
        Patient currentPatient = buildPatient();
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(currentPatient));
        when(patientRepository.existsByEmail(newEmail)).thenReturn(true);
        //when
        PatientAlreadyExistsException result = assertThrows(PatientAlreadyExistsException.class, () -> patientService.editPatient(email, newPatientData));
        //then
        assertEquals("Patient with email: %s already exists.".formatted(newEmail), result.getMessage());
    }

    @Test
    void editPatient_PatientDataContainsNewIdCardNo_ThrowsPatientIllegalDataException() {
        //given
        String email = "email";
        String newEmail = "new email";
        FullPatientDataDTO newPatientData = FullPatientDataDTO.builder()
                .email(newEmail)
                .password("new password")
                .idCardNo("new idCardNo")
                .firstName("new firstName")
                .lastName("new lastName")
                .phoneNumber("new phoneNumber")
                .birthday(LocalDate.of(2012, 11, 11))
                .build();
        Patient currentPatient = buildPatient();
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(currentPatient));
        when(patientRepository.existsByEmail(newEmail)).thenReturn(false);
        //when
        PatientIllegalDataException result = assertThrows(PatientIllegalDataException.class, () -> patientService.editPatient(email, newPatientData));
        //then
        assertEquals("ID card number cannot be changed.", result.getMessage());
    }

    @Test
    void editPatient_PatientDataIsCorrect_EditsPatient() {
        //given
        String email = "email";
        String newEmail = "new email";
        FullPatientDataDTO newPatientData = FullPatientDataDTO.builder()
                .email(newEmail)
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
        when(patientRepository.existsByEmail(newEmail)).thenReturn(false);
        //when
        PatientDTO result = patientService.editPatient(email, newPatientData);
        //then
        assertInstanceOf(PatientDTO.class, result);
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
        PatientIllegalDataException result = assertThrows(PatientIllegalDataException.class, () -> patientService.editPatientPassword(email, password));
        //then
        assertEquals("Password cannot be set to null.", result.getMessage());
    }

    @Test
    void editPatientPassword_PatientDoesNotExist_ThrowsPatientNotFoundException() {
        //given
        String email = "email";
        String password = "password";
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());
        //when
        PatientNotFoundException result = assertThrows(PatientNotFoundException.class, () -> patientService.editPatientPassword(email, password));
        //then
        assertEquals("Patient with email %s does not exist.".formatted(email), result.getMessage());
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
}

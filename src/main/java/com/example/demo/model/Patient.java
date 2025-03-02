package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Patient {
    private String email;
    private String password;
    private String idCardNo;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthday;

    public Patient copy() {
        return new Patient(email, password, idCardNo, firstName, lastName, phoneNumber, birthday);
    }
}

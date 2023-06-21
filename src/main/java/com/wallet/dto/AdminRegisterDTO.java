package com.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRegisterDTO {

    private String userName;
    private String password;
    private String fullName;
    private String email;
    private LocalDate dob;
    private String image;
    private String phone;


}
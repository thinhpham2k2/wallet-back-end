package com.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateDTO implements Serializable {

    private String fullName;
    private LocalDate dob;
    private String image;
    private String phone;

}

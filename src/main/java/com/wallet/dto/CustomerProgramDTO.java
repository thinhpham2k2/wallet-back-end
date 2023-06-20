package com.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProgramDTO {

    private String customerId;
    private String fullName;
    private String email;
    private LocalDate dob;
    private String image;
    private String phone;
}

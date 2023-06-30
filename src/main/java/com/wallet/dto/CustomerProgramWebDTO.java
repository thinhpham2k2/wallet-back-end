package com.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProgramWebDTO {

    private String customerId;
    private String fullName;
    private String email;
    private LocalDate dob;
    private MultipartFile image;
    private String phone;
}

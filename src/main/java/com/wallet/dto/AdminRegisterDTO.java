package com.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRegisterDTO implements Serializable {

    private String userName;
    private String password;
    private String fullName;
    private String email;
    private LocalDate dob;
    private MultipartFile image;
    private String phone;
    
}

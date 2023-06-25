package com.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartnerRegisterDTO implements Serializable {

    private String userName;
    private String password;
    private String fullName;
    private String code;
    private String email;
    private MultipartFile image;
    private String phone;
    private String address;

}

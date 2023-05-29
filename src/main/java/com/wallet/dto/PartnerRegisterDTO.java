package com.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartnerRegisterDTO {

    private String userName;
    private String password;
    private String fullName;
    private String code;
    private String email;
    private String image;
    private String phone;
    private String address;

}

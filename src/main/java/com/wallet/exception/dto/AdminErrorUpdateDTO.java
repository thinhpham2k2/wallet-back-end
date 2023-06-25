package com.wallet.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminErrorUpdateDTO {

    private String id;
    private String fullName;
    private String dob;
    private String phone;
    private String image;

}

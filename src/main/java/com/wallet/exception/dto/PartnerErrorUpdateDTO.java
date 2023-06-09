package com.wallet.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartnerErrorUpdateDTO {

    private String fullName;
    private String phone;
    private String image;
    private String state;
}

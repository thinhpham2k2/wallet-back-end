package com.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerUpdateDTO {

    private String username;
    private String fullName;
    private String code;
    private String image;
    private String email;
    private String phone;
    private String address;
    private Boolean state;
    private Boolean status;
}

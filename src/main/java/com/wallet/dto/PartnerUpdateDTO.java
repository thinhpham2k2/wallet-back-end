package com.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerUpdateDTO implements Serializable {

    private String fullName;
    private String image;
    private String phone;
    private String address;
    private Boolean state;

}

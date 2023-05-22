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
public class PartnerDTO implements Serializable {

    private Long id;
    private String userName;
    private String fullName;
    private String code;
    private String email;
    private String image;
    private String phone;
    private String address;
    private Boolean state;
    private Boolean status;

}

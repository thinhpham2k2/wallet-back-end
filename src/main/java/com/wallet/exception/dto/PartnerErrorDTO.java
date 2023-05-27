package com.wallet.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerErrorDTO {

    private String userName;
    private String fullName;
    private String code;
    private String email;
    private String password;

}

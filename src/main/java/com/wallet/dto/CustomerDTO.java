package com.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO implements Serializable {

    private Long id;
    private String customerId;
    private String fullName;
    private String email;
    private LocalDate dob;
    private String image;
    private String phone;
    private Boolean state;
    private Boolean status;
    private Long partnerId;
    private String partnerName;

}

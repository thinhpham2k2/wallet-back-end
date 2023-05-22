package com.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipDTO implements Serializable {

    private Long id;
    private LocalDate dateCreated;
    private BigDecimal totalReceipt;
    private BigDecimal totalExpenditure;
    private Boolean state;
    private Boolean status;
    private Long levelId;
    private String level;
    private Long customerId;
    private String customerName;
    private Long programId;
    private String programName;

}

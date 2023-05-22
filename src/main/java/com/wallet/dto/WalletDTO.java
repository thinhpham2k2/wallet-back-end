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
public class WalletDTO implements Serializable {

    private Long id;
    private BigDecimal balance;
    private BigDecimal totalReceipt;
    private BigDecimal totalExpenditure;
    private LocalDate dateCreated;
    private LocalDate dateUpdated;
    private Boolean state;
    private Boolean status;
    private Long membershipId;
    private Long typeId;
    private String type;

}

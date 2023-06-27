package com.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestExtraDTO {

    private Long id;
    private BigDecimal amount;
    private LocalDate dateCreated;
    private LocalDate dateUpdated;
    private String description;
    private Boolean state;
    private Boolean status;
    private Long partnerId;
    private String partnerName;
    private Long typeId;
    private String type;
    private List<TransactionDTO> transactionList;
}

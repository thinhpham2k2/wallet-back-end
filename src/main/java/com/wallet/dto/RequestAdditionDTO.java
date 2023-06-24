package com.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestAdditionDTO {

    private String customerId;
    private BigDecimal amount;
    private String description;
    private String token;
    private long walletId;
}

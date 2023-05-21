package com.wallet.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.wallet.entity.Request;
import com.wallet.entity.Type;
import com.wallet.entity.Wallet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO implements Serializable {

    private Long id;
    private BigDecimal amount;
    private LocalDate dateCreated;
    private LocalDate dateUpdated;
    private String description;
    private Boolean state;
    private Boolean status;
    private Long typeId;
    private String type;
    private Long walletId;
    private Long requestId;

}

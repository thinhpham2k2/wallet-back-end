package com.wallet.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.wallet.entity.Customer;
import com.wallet.entity.Level;
import com.wallet.entity.Program;
import com.wallet.entity.Wallet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

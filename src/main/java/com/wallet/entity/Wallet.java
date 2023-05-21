package com.wallet.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Entity(name = "Wallet")
@Table(
        name = "tblWallet"
)
public class Wallet implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Column(
            name = "balance"
    )
    private BigDecimal balance;

    @Column(
            name = "total_receipt"
    )
    private BigDecimal totalReceipt;

    @Column(
            name = "total_expenditure"
    )
    private BigDecimal totalExpenditure;

    @Column(
            name = "date_created"
    )
    private LocalDate dateCreated;

    @Column(
            name = "date_updated"
    )
    private LocalDate dateUpdated;

    @Column(name = "state")
    private Boolean state;

    @Column(name = "status")
    private Boolean status;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "member_id")
    private Membership membership;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "type_id")
    private WalletType type;

    @OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Transaction> transactionList;
}

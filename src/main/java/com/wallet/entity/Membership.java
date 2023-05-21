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
@Entity(name = "Membership")
@Table(
        name = "tblMembership"
)
public class Membership implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Column(
            name = "date_created"
    )
    private LocalDate dateCreated;

    @Column(
            name = "total_receipt"
    )
    private BigDecimal totalReceipt;

    @Column(
            name = "total_expenditure"
    )
    private BigDecimal totalExpenditure;

    @Column(name = "state")
    private Boolean state;

    @Column(name = "status")
    private Boolean status;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "level_id")
    private Level level;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "program_id")
    private Program program;

    @OneToMany(mappedBy = "membership", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Wallet> walletList;
}

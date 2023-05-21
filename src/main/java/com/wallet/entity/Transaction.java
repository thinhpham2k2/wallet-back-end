package com.wallet.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Entity(name = "Transaction")
@Table(
        name = "tblTransaction"
)
public class Transaction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Column(
            name = "amount"
    )
    private BigDecimal amount;

    @Column(
            name = "date_created"
    )
    private LocalDate dateCreated;

    @Column(
            name = "date_updated"
    )
    private LocalDate dateUpdated;

    @Lob
    @Nationalized
    @Column(
            name = "description"
    )
    private String description;

    @Column(name = "state")
    private Boolean state;

    @Column(name = "status")
    private Boolean status;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "type_id")
    private Type type;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "request_id")
    private Request request;
}

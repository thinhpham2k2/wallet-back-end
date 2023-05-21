package com.wallet.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Request")
@Table(
        name = "tblRequest"
)
public class Request implements Serializable {
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
    @JoinColumn(name = "partner_id")
    private Partner partner;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "type_id")
    private RequestType type;

    @OneToMany(mappedBy = "request", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Transaction> transactionList;
}

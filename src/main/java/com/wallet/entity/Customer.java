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
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Customer")
@Table(
        name = "tblCustomer"
)
public class Customer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Lob
    @Nationalized
    @Column(
            name = "customer_id"
    )
    private String customerId;

    @Lob
    @Nationalized
    @Column(
            name = "full_name"
    )
    private String fullName;

    @Column(
            name = "email",
            length = 320
    )
    private String email;

    @Column(
            name = "date_of_birth"
    )
    private LocalDate dob;

    @Lob
    @Nationalized
    @Column(
            name = "image"
    )
    private String image;

    @Column(
            name = "phone",
            length = 17
    )
    private String phone;

    @Column(name = "state")
    private boolean state;

    @Column(name = "status")
    private boolean status;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "partner_id")
    private Partner partner;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Membership> membershipList;

}

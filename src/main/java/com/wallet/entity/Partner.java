package com.wallet.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Partner")
@Table(
        name = "tblPartner",
        uniqueConstraints = {
                @UniqueConstraint(name = "partner_user_name_unique", columnNames = "user_name")
        }
)
public class Partner implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Column(
            name = "user_name",
            updatable = false,
            length = 50
    )
    private String userName;

    @Column(
            name = "password"
    )
    private  String password;

    @Lob
    @Nationalized
    @Column(
            name = "full_name"
    )
    private String fullName;

    @Column(
            name = "code",
            length = 20
    )
    private String code;

    @Column(
            name = "email",
            length = 320
    )
    private String email;

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

    @Lob
    @Nationalized
    @Column(
            name = "address"
    )
    private String address;

    @Column(name = "state")
    private Boolean state;

    @Column(name = "status")
    private Boolean status;

    @OneToMany(mappedBy = "partner", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Customer> customerList;

    @OneToMany(mappedBy = "partner", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Program> programList;

    @OneToMany(mappedBy = "partner", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Request> requestList;
}

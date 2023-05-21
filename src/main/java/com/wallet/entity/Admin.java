package com.wallet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Admin")
@Table(
        name = "tblAdmin",
        uniqueConstraints = {
                @UniqueConstraint(name = "user_name_unique", columnNames = "user_name"),
                @UniqueConstraint(name = "user_email_unique", columnNames = "email"),
                @UniqueConstraint(name = "user_phone_unique", columnNames = "phone")
        }
)
public class Admin implements Serializable {
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

    @Column(name = "status")
    private Boolean status;

}

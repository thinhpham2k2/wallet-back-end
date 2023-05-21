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
@Entity(name = "Program")
@Table(
        name = "tblProgram"
)
public class Program implements Serializable {
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
            name = "program_name"
    )
    private String programName;

    @Lob
    @Nationalized
    @Column(
            name = "description"
    )
    private String description;

    @Lob
    @Nationalized
    @Column(
            name = "token"
    )
    private String token;

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
    @JoinColumn(name = "partner_id")
    private Partner partner;

    @OneToMany(mappedBy = "program", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<ProgramLevel> programLevelList;

    @OneToMany(mappedBy = "program", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Membership> membershipList;
}

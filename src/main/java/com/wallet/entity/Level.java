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
@Entity(name = "Level")
@Table(
        name = "tblLevel"
)
public class Level implements Serializable {
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
            name = "level"
    )
    private String level;

    @Lob
    @Nationalized
    @Column(
            name = "description"
    )
    private String description;

    @Column(name = "status")
    private boolean status;

    @OneToMany(mappedBy = "level", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<ProgramLevel> programLevelList;

    @OneToMany(mappedBy = "level", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Membership> membershipList;
}

package com.wallet.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "RequestType")
@Table(
        name = "tblRequestType"
)
public class RequestType implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Column(name = "type")
    private Boolean type;

    @Column(name = "status")
    private Boolean status;

    @OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Request> requestList;
}

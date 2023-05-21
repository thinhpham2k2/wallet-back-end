package com.wallet.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.wallet.entity.Partner;
import com.wallet.entity.RequestType;
import com.wallet.entity.Transaction;
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
public class RequestDTO implements Serializable {

    private Long id;
    private BigDecimal amount;
    private LocalDate dateCreated;
    private LocalDate dateUpdated;
    private String description;
    private Boolean state;
    private Boolean status;
    private Long partnerId;
    private String partnerName;
    private Long typeId;
    private String type;

}

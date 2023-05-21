package com.wallet.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.wallet.entity.Membership;
import com.wallet.entity.Partner;
import com.wallet.entity.ProgramLevel;
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
public class ProgramDTO  implements Serializable {

    private Long id;
    private String programName;
    private String description;
    private String token;
    private LocalDate dateCreated;
    private LocalDate dateUpdated;
    private Boolean state;
    private Boolean status;
    private Long partnerId;
    private String partnerName;

}

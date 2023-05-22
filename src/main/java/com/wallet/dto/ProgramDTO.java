package com.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

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

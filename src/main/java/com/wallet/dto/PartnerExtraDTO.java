package com.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerExtraDTO {

    private int numOfCustomers;
    private PartnerDTO partner;
    private List<ProgramDTO> programList;
}
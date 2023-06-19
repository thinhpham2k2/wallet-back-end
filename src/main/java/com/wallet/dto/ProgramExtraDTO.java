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
public class ProgramExtraDTO {

    private int numOfMembers;
    private ProgramDTO program;
    private PartnerDTO partner;
    private List<LevelDTO> levelList;
}

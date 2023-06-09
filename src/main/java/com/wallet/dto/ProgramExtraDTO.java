package com.wallet.dto;

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
public class ProgramExtraDTO implements Serializable {

    private int numOfMembers;
    private ProgramDTO program;
    private PartnerDTO partner;
    private List<LevelDTO> levelList;
}

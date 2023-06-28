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
public class ProgramCreationDTO implements Serializable {

    private String programName;
    private String description;
    private Integer numberOfWeek;
    private List<LevelCreationDTO> levelList;
}

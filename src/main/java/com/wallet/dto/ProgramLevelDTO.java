package com.wallet.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.wallet.entity.Level;
import com.wallet.entity.Program;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramLevelDTO implements Serializable {

    private Long id;
    private String description;
    private Boolean state;
    private Boolean status;
    private Long levelId;
    private String level;
    private Long programId;
    private String programName;

}

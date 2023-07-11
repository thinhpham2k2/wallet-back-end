package com.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TitleDTO implements Serializable {

    private Long numberOfCustomer;

    private Long numberOfMember;

    private Long numberOfTransaction;

    private ProgramDTO program;
}

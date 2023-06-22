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
public class MembershipExtraDTO implements Serializable {


    private CustomerDTO customer;

    private MembershipDTO membership;

    private PartnerDTO partner;

    private List<LevelDTO> levelList;

    private List<WalletDTO> walletList;
}

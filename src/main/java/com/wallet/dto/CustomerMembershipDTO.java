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
public class CustomerMembershipDTO {

    private CustomerDTO customer;

    private MembershipDTO membership;

    private LevelDTO nextLevel;

    private List<LevelDTO> levelList;

    private List<WalletDTO> walletList;
}

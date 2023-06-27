package com.wallet.service.interfaces;

import com.wallet.dto.WalletDTO;

import java.util.List;

public interface IWalletService {

    List<WalletDTO> findAllByProgramTokenAndCustomerId(String token, String customerId);
}

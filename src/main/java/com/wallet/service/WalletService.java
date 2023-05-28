package com.wallet.service;

import com.wallet.repository.WalletRepository;
import com.wallet.service.interfaces.IWalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class WalletService implements IWalletService {

    private final WalletRepository walletRepository;

}

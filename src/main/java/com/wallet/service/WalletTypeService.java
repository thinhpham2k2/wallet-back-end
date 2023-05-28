package com.wallet.service;

import com.wallet.repository.WalletTypeRepository;
import com.wallet.service.interfaces.IWalletTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class WalletTypeService implements IWalletTypeService {

    private final WalletTypeRepository walletTypeRepository;

}

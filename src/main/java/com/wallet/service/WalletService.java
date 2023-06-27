package com.wallet.service;

import com.wallet.dto.WalletDTO;
import com.wallet.mapper.WalletMapper;
import com.wallet.repository.WalletRepository;
import com.wallet.service.interfaces.IWalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class WalletService implements IWalletService {

    private final WalletRepository walletRepository;

    @Override
    public List<WalletDTO> findAllByProgramTokenAndCustomerId(String token, String customerId) {
        return walletRepository.findAllByProgramTokenAndCustomerId(true, customerId, token).stream().map(WalletMapper.INSTANCE::toDTO).collect(Collectors.toList());
    }
}

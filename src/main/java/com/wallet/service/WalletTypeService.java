package com.wallet.service;

import com.wallet.dto.WalletTypeDTO;
import com.wallet.mapper.WalletTypeMapper;
import com.wallet.repository.WalletTypeRepository;
import com.wallet.service.interfaces.IWalletTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class WalletTypeService implements IWalletTypeService {

    private final WalletTypeRepository walletTypeRepository;

    @Override
    public List<WalletTypeDTO> getAllType() {
        return walletTypeRepository.getAllByStatus(true).stream().map(WalletTypeMapper.INSTANCE::toDTO).collect(Collectors.toList());
    }
}

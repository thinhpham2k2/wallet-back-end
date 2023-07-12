package com.wallet.service;

import com.wallet.dto.WalletDTO;
import com.wallet.entity.Membership;
import com.wallet.entity.Program;
import com.wallet.entity.Wallet;
import com.wallet.entity.WalletType;
import com.wallet.mapper.WalletMapper;
import com.wallet.repository.ProgramRepository;
import com.wallet.repository.WalletRepository;
import com.wallet.repository.WalletTypeRepository;
import com.wallet.service.interfaces.IWalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class WalletService implements IWalletService {

    private final WalletRepository walletRepository;

    private final ProgramRepository programRepository;

    private final WalletTypeRepository walletTypeRepository;

    @Override
    public List<WalletDTO> findAllByProgramTokenAndCustomerId(String token, String customerId) {
        return walletRepository.findAllByProgramTokenAndCustomerId(true, customerId, token).stream().map(WalletMapper.INSTANCE::toDTO).collect(Collectors.toList());
    }

    @Override
    public WalletDTO createWallet(String token, long membershipId, long typeWalletId) {
        Program program = programRepository.getProgramByStatusAndToken(true, token);
        if (program != null) {
            Optional<Membership> membership = program.getMembershipList().stream().filter(m -> m.getId().equals(membershipId)).findFirst();
            if (membership.isPresent()) {
                Optional<WalletType> walletType = walletTypeRepository.findByIdAndStatus(typeWalletId, true);
                if (walletType.isPresent()) {
                    if (!membership.get().getWalletList().stream().filter(w -> w.getStatus().equals(true)).map(w -> w.getType().getId()).toList().contains(typeWalletId)) {
                        Wallet wallet = new Wallet();
                        wallet.setBalance(BigDecimal.ZERO);
                        wallet.setTotalReceipt(BigDecimal.ZERO);
                        wallet.setTotalExpenditure(BigDecimal.ZERO);
                        wallet.setDateCreated(LocalDate.now());
                        wallet.setDateUpdated(LocalDate.now());
                        wallet.setState(true);
                        wallet.setStatus(true);
                        wallet.setType(walletType.get());
                        wallet.setMembership(membership.get());
                        return WalletMapper.INSTANCE.toDTO(walletRepository.save(wallet));
                    } else {
                        throw new InvalidParameterException("Membership already has this wallet.");
                    }
                } else {
                    throw new InvalidParameterException("Not found wallet type");
                }
            } else {
                throw new InvalidParameterException("Not found membership");
            }
        } else {
            throw new InvalidParameterException("Invalid program token");
        }
    }
}

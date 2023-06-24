package com.wallet.service;

import com.wallet.dto.RequestDTO;
import com.wallet.dto.RequestSubtractionDTO;
import com.wallet.entity.Membership;
import com.wallet.entity.Partner;
import com.wallet.entity.Wallet;
import com.wallet.jwt.JwtTokenProvider;
import com.wallet.repository.MembershipRepository;
import com.wallet.repository.PartnerRepository;
import com.wallet.repository.ProgramRepository;
import com.wallet.repository.RequestRepository;
import com.wallet.service.interfaces.IRequestService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Part;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RequestService implements IRequestService {

    private final RequestRepository requestRepository;

    private final PartnerRepository partnerRepository;

    private final ProgramRepository programRepository;

    private final MembershipRepository membershipRepository;

    @Override
    public RequestDTO createRequestSubtraction(RequestSubtractionDTO subtraction, String token) {
        String userName;
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Invalid JWT token");
        }
        Optional<Partner> partner = partnerRepository.findPartnerByUserNameAndStatus(userName, true);
        if (partner.isPresent() && programRepository.existsProgramByStatusAndToken(true, token)) {
            Optional<Membership> membership = membershipRepository.findByCustomerIdAndStatus(true, subtraction.getCustomerId(), token);
            if (membership.isPresent()) {
                List<Wallet> walletList = membership.get().getWalletList().stream().filter(w -> w.getStatus().equals(true)).toList();
                Set<Long> walletsOwner = walletList.stream().map(Wallet::getId).collect(Collectors.toSet());
                if (walletsOwner.containsAll(subtraction.getWalletIds())) {
                    Set<Long> commonIds = new HashSet<>(walletsOwner);
                    commonIds.retainAll(subtraction.getWalletIds());
                    List<Wallet> wallets = walletList.stream().filter(w -> commonIds.contains(w.getId())).toList();
                    BigDecimal totalBalance  = wallets.stream().map(Wallet::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
                    if (totalBalance.compareTo(subtraction.getAmount()) >= 0) {
                        //Handle
                    }
                    else {
                        throw new InvalidParameterException("Not enough balance to make a transaction");
                    }
                }
                else {
                    throw new InvalidParameterException("Invalid wallet");
                }
            } else {
                throw new InvalidParameterException("Invalid customer information");
            }
        } else {
            throw new InvalidParameterException("Invalid partner information");
        }
        return null;
    }
}

package com.wallet.service;

import com.wallet.dto.CustomerMembershipDTO;
import com.wallet.dto.MembershipDTO;
import com.wallet.entity.Level;
import com.wallet.entity.Membership;
import com.wallet.entity.ProgramLevel;
import com.wallet.entity.Wallet;
import com.wallet.jwt.JwtTokenProvider;
import com.wallet.mapper.CustomerMapper;
import com.wallet.mapper.LevelMapper;
import com.wallet.mapper.MembershipMapper;
import com.wallet.mapper.WalletMapper;
import com.wallet.repository.MembershipRepository;
import com.wallet.repository.ProgramLevelRepository;
import com.wallet.repository.WalletRepository;
import com.wallet.service.interfaces.IMembershipService;
import com.wallet.service.interfaces.IPagingService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MembershipService implements IMembershipService {

    private final MembershipRepository membershipRepository;

    private final ProgramLevelRepository programLevelRepository;

    private final WalletRepository walletRepository;

    private final IPagingService pagingService;

    @Override
    public Page<MembershipDTO> getMemberList(boolean status, List<Long> partnerId, List<Long> programId, String search, String sort, int page, int limit) {
        if (limit < 1) throw new InvalidParameterException("Page size must not be less than one!");
        if (page < 0) throw new InvalidParameterException("Page number must not be less than zero!");
        List<Sort.Order> order = new ArrayList<>();
        Set<String> sourceFieldList = pagingService.getAllFields(Membership.class);
        String[] subSort = sort.split(",");
        if (pagingService.checkPropertPresent(sourceFieldList, subSort[0])) {
            order.add(new Sort.Order(pagingService.getSortDirection(subSort[1]), transferProperty(subSort[0])));
        } else {
            throw new InvalidParameterException(subSort[0] + " is not a propertied of Membership!");
        }
        Pageable pageable = PageRequest.of(page, limit).withSort(Sort.by(order));
        Page<Membership> pageResult = membershipRepository.getMemberList(true, partnerId, programId, search, pageable);
        return new PageImpl<>(pageResult.getContent().stream().map(MembershipMapper.INSTANCE::toDTO).collect(Collectors.toList()), pageResult.getPageable(), pageResult.getTotalElements());
    }

    @Override
    public Page<MembershipDTO> getMemberListForPartner(boolean status, String token, List<Long> programId, String search, String sort, int page, int limit) {
        if (limit < 1) throw new InvalidParameterException("Page size must not be less than one!");
        if (page < 0) throw new InvalidParameterException("Page number must not be less than zero!");
        String userName;
        List<Sort.Order> order = new ArrayList<>();
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Expired JWT token");
        }
        Set<String> sourceFieldList = pagingService.getAllFields(Membership.class);
        String[] subSort = sort.split(",");
        if (pagingService.checkPropertPresent(sourceFieldList, subSort[0])) {
            order.add(new Sort.Order(pagingService.getSortDirection(subSort[1]), transferProperty(subSort[0])));
        } else {
            throw new InvalidParameterException(subSort[0] + " is not a propertied of Membership!");
        }
        Pageable pageable = PageRequest.of(page, limit).withSort(Sort.by(order));
        Page<Membership> pageResult = membershipRepository.getMemberListForPartner(true, userName, programId, search, pageable);
        return new PageImpl<>(pageResult.getContent().stream().map(MembershipMapper.INSTANCE::toDTO).collect(Collectors.toList()), pageResult.getPageable(), pageResult.getTotalElements());
    }

    private static String transferProperty(String property) {
        return switch (property) {
            case "level" -> "level.condition";
            case "customer" -> "customer.fullName";
            case "program" -> "program.programName";
            case "partner" -> "program.partner.fullName";
            default -> property;
        };
    }

    @Override
    public CustomerMembershipDTO getCustomerMembershipInform(String token, String customerId) {
        CustomerMembershipDTO customerMember = new CustomerMembershipDTO();
        Membership membership = membershipRepository.getCustomerMembershipInform(true, token, customerId);
        if (membership != null) {

            customerMember.setMembership(MembershipMapper.INSTANCE.toDTO(membership));
            customerMember.setCustomer(CustomerMapper.INSTANCE.toDTO(membership.getCustomer()));

            ProgramLevel programLevel = programLevelRepository.getNextLevel(true, token, membership.getLevel().getCondition());
            if (programLevel != null) {
                customerMember.setNextLevel(LevelMapper.INSTANCE.toDTO(programLevel.getLevel()));
            }

            List<ProgramLevel> programLevelList = programLevelRepository.getLeveListByProgramToken(true, token);
            if(!programLevelList.isEmpty()) {
                List<Level> levelList = programLevelList.stream().map(ProgramLevel::getLevel).toList();
                customerMember.setLevelList(levelList.stream().map(LevelMapper.INSTANCE::toDTO).collect(Collectors.toList()));
            }

            List<Wallet> wallets = membership.getWalletList().stream().filter(w -> w.getStatus().equals(true)).collect(Collectors.toList());
            if (!wallets.isEmpty()) {
               customerMember.setWalletList(wallets.stream().map(WalletMapper.INSTANCE::toDTO).collect(Collectors.toList()));
            }
        } else {
            throw new InvalidParameterException("Not found membership information !");
        }
        return customerMember;
    }
}

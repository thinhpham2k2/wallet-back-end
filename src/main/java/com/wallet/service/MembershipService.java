package com.wallet.service;

import com.wallet.dto.*;
import com.wallet.entity.*;
import com.wallet.jwt.JwtTokenProvider;
import com.wallet.mapper.*;
import com.wallet.repository.*;
import com.wallet.service.interfaces.IMembershipService;
import com.wallet.service.interfaces.IPagingService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MembershipService implements IMembershipService {

    private final MembershipRepository membershipRepository;

    private final ProgramLevelRepository programLevelRepository;

    private final ProgramRepository programRepository;

    private final CustomerRepository customerRepository;

    private final WalletRepository walletRepository;

    private final WalletTypeRepository walletTypeRepository;

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
            throw new InvalidParameterException("Invalid JWT token");
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
            if (!programLevelList.isEmpty()) {
                List<Level> levelList = programLevelList.stream().map(ProgramLevel::getLevel).toList();
                customerMember.setLevelList(levelList.stream().map(LevelMapper.INSTANCE::toDTO).collect(Collectors.toList()));
            }

            List<Wallet> wallets = membership.getWalletList().stream().filter(w -> w.getStatus().equals(true)).toList();
            if (!wallets.isEmpty()) {
                customerMember.setWalletList(wallets.stream().map(WalletMapper.INSTANCE::toDTO).collect(Collectors.toList()));
            }
        } else {
            throw new InvalidParameterException("Not found membership information !");
        }
        return customerMember;
    }

    @Override
    public CustomerMembershipDTO createCustomer(String token, CustomerProgramDTO customer) {
        Program program = programRepository.getProgramByStatusAndToken(true, token);
        if (program != null) {
            CustomerDTO customerDTO = new CustomerDTO(null, customer.getCustomerId(), customer.getFullName(), customer.getEmail(), customer.getDob(), customer.getImage(), customer.getPhone(), true, true, program.getPartner().getId(), null);
            long count = program.getPartner().getCustomerList().stream().filter(p -> p.getCustomerId().equals(customer.getCustomerId())).count();

            //Get Level
            Optional<Level> level = program.getProgramLevelList().stream().map(ProgramLevel::getLevel).filter(l -> l.getCondition().compareTo(BigDecimal.ZERO) == 0).findFirst();
            if (count == 0 && level.isPresent()) {
                CustomerMembershipDTO customerMember = new CustomerMembershipDTO();
                //Create Customer
                Customer customerEntity = customerRepository.save(CustomerMapper.INSTANCE.toEntity(customerDTO));
                customerEntity.getPartner().setFullName(program.getPartner().getFullName());

                //Create Membership
                Membership membershipEntity = membershipRepository.save(MembershipMapper.INSTANCE.toEntity(new MembershipDTO(null, LocalDate.now(), BigDecimal.valueOf(0L), BigDecimal.valueOf(0L), true, true, level.get().getId(), level.get().getLevel(), customerEntity.getId(), null, program.getId(), program.getProgramName())));
                membershipEntity.getLevel().setLevel(level.get().getLevel());
                membershipEntity.getCustomer().setFullName(customer.getFullName());
                membershipEntity.getProgram().setProgramName(program.getProgramName());

                //Create Wallet
                Wallet walletEntity = walletRepository.save(WalletMapper.INSTANCE.toEntity(new WalletDTO(null, BigDecimal.valueOf(0L), BigDecimal.valueOf(0L), BigDecimal.valueOf(0L), LocalDate.now(), LocalDate.now(), true, true, membershipEntity.getId(), walletTypeRepository.getWalletTypeByStatusAndType(true, "Main wallet").getId(), "Main wallet")));
                walletEntity.getType().setType("Main wallet");

                //Create Customer Membership DTO
                customerMember.setCustomer(CustomerMapper.INSTANCE.toDTO(customerEntity));

                customerMember.setMembership(MembershipMapper.INSTANCE.toDTO(membershipEntity));

                List<WalletDTO> walletList = new ArrayList<>();
                walletList.add(WalletMapper.INSTANCE.toDTO(walletEntity));
                customerMember.setWalletList(walletList);

                ProgramLevel programLevel = programLevelRepository.getNextLevel(true, token, BigDecimal.valueOf(0L));
                if (programLevel != null) {
                    customerMember.setNextLevel(LevelMapper.INSTANCE.toDTO(programLevel.getLevel()));
                }

                List<ProgramLevel> programLevelList = programLevelRepository.getLeveListByProgramToken(true, token);
                if (!programLevelList.isEmpty()) {
                    List<Level> levels = programLevelList.stream().map(ProgramLevel::getLevel).toList();
                    customerMember.setLevelList(levels.stream().map(LevelMapper.INSTANCE::toDTO).collect(Collectors.toList()));
                }

                return customerMember;
            }
        }
        return null;
    }

    @Override
    public MembershipExtraDTO getMemberById(String token, long memberId, boolean isAdmin) {
        Optional<Membership> membership;
        if (isAdmin) {
            membership = membershipRepository.findByStatusAndId(true, memberId);
        } else {
            String userName;
            try {
                JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
                userName = jwtTokenProvider.getUserNameFromJWT(token);
            } catch (ExpiredJwtException e) {
                throw new InvalidParameterException("Invalid JWT token");
            }
            membership = membershipRepository.findByStatusAndId(true, memberId, userName);
        }
        if (membership.isPresent()) {
            MembershipExtraDTO membershipExtra = new MembershipExtraDTO();

            //Set Customer
            membershipExtra.setCustomer(CustomerMapper.INSTANCE.toDTO(membership.get().getCustomer()));
            //Set Membership
            membershipExtra.setMembership(MembershipMapper.INSTANCE.toDTO(membership.get()));
            //Set Partner
            membershipExtra.setPartner(PartnerMapper.INSTANCE.toDTO(membership.get().getCustomer().getPartner()));
            //Set Wallet List
            membershipExtra.setWalletList(membership.get().getWalletList().stream().map(WalletMapper.INSTANCE::toDTO).collect(Collectors.toList()));
            //Set Level List
            membershipExtra.setLevelList(membership.get().getProgram().getProgramLevelList().stream().map(ProgramLevel::getLevel).filter(l -> l.getStatus().equals(true)).map(LevelMapper.INSTANCE::toDTO).collect(Collectors.toList()));

            return membershipExtra;
        } else {
            throw new InvalidParameterException("Not found membership!");
        }
    }
}

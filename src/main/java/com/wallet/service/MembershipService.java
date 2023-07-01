package com.wallet.service;

import com.wallet.dto.*;
import com.wallet.entity.*;
import com.wallet.jwt.JwtTokenProvider;
import com.wallet.mapper.*;
import com.wallet.repository.*;
import com.wallet.service.interfaces.IFileService;
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

    private final PartnerRepository partnerRepository;

    private final ProgramLevelRepository programLevelRepository;

    private final ProgramRepository programRepository;

    private final CustomerRepository customerRepository;

    private final WalletRepository walletRepository;

    private final WalletTypeRepository walletTypeRepository;

    private final IPagingService pagingService;

    private final IFileService fileService;

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
    public CustomerMembershipDTO createMembership(String token, String customerId) {
        String userName;
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Expired JWT token");
        }
        Optional<Partner> partner = partnerRepository.findPartnerByUserNameAndStatus(userName, true);
        if (partner.isPresent()) {
            Optional<Program> program = partner.get().getProgramList().stream().filter(p -> p.getDateUpdated().isAfter(LocalDate.now().minusDays(1)) && p.getStatus().equals(true) && p.getState().equals(true)).findFirst();
            if (program.isPresent()) {
                Optional<Customer> customer = program.get().getPartner().getCustomerList().stream().filter(p -> p.getCustomerId().equals(customerId) && p.getStatus().equals(true)).findFirst();
                if (customer.isPresent()) {
                    long countMem = program.get().getMembershipList().stream().filter(m -> m.getCustomer().getCustomerId().equals(customerId) && m.getStatus().equals(true)).count();
                    //Get Level
                    Optional<Level> level = program.get().getProgramLevelList().stream().map(ProgramLevel::getLevel).filter(l -> l.getCondition().compareTo(BigDecimal.ZERO) == 0).findFirst();
                    if (countMem == 0) {
                        if (level.isPresent()) {
                            CustomerMembershipDTO customerMember = new CustomerMembershipDTO();

                            //Create Membership
                            Membership membershipEntity = membershipRepository.save(MembershipMapper.INSTANCE.toEntity(new MembershipDTO(null, LocalDate.now(), BigDecimal.valueOf(0L), BigDecimal.valueOf(0L), true, true, level.get().getId(), level.get().getLevel(), customer.get().getId(), null, program.get().getId(), program.get().getProgramName())));
                            membershipEntity.getLevel().setLevel(level.get().getLevel());
                            membershipEntity.getCustomer().setFullName(customer.get().getFullName());
                            membershipEntity.getProgram().setProgramName(program.get().getProgramName());

                            //Create Wallet
                            Wallet walletEntity = walletRepository.save(WalletMapper.INSTANCE.toEntity(new WalletDTO(null, BigDecimal.valueOf(0L), BigDecimal.valueOf(0L), BigDecimal.valueOf(0L), LocalDate.now(), LocalDate.now(), true, true, membershipEntity.getId(), walletTypeRepository.getWalletTypeByStatusAndType(true, "Main wallet").getId(), "Main wallet")));
                            walletEntity.getType().setType("Main wallet");

                            //Create Customer Membership DTO
                            customerMember.setCustomer(CustomerMapper.INSTANCE.toDTO(customer.get()));

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
                        } else {
                            throw new InvalidParameterException("Has not found a valid level");
                        }
                    } else {
                        throw new InvalidParameterException("Customer already has a membership for this program");
                    }
                } else {
                    throw new InvalidParameterException("Not found customer");
                }
            } else {
                throw new InvalidParameterException("Invalid program");
            }
        } else {
            throw new InvalidParameterException("Invalid partner");
        }
    }

    @Override
    public CustomerMembershipDTO createCustomerWeb(String token, CustomerProgramWebDTO customer) {
        String userName;
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Expired JWT token");
        }
        Optional<Partner> partner = partnerRepository.findPartnerByUserNameAndStatus(userName, true);
        if (partner.isPresent()) {
            if (customer.getCustomerId() != null) {
                String linkImg = null;
                if (customer.getImage() != null) {
                    try {
                        linkImg = fileService.upload(customer.getImage());
                    } catch (Exception e) {
                        throw new InvalidParameterException("Invalid image file !");
                    }
                }
                CustomerDTO customerDTO = new CustomerDTO(null, customer.getCustomerId(), customer.getFullName(), customer.getEmail(), customer.getDob(), linkImg, customer.getPhone(), true, true, partner.get().getId(), null);
                long count = partner.get().getCustomerList().stream().filter(p -> p.getCustomerId().equals(customer.getCustomerId()) && p.getStatus().equals(true)).count();
                if (count == 0) {
                    CustomerMembershipDTO customerMember = new CustomerMembershipDTO();
                    //Create Customer
                    Customer customerEntity = customerRepository.save(CustomerMapper.INSTANCE.toEntity(customerDTO));
                    customerEntity.getPartner().setFullName(partner.get().getFullName());

                    //Create Customer Membership DTO
                    customerMember.setCustomer(CustomerMapper.INSTANCE.toDTO(customerEntity));
                    customerMember.setMembership(null);
                    customerMember.setWalletList(null);
                    customerMember.setNextLevel(null);
                    customerMember.setLevelList(null);
                    return customerMember;
                } else {
                    throw new InvalidParameterException("Customer already exists");
                }
            } else {
                throw new InvalidParameterException("Customer id cannot be blank");
            }
        } else {
            throw new InvalidParameterException("Invalid partner");
        }
    }

    @Override
    public CustomerMembershipDTO createCustomer(String token, CustomerProgramDTO customer) {
        String userName;
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Expired JWT token");
        }
        Optional<Partner> partner = partnerRepository.findPartnerByUserNameAndStatus(userName, true);
        if (partner.isPresent()) {
            if (customer.getCustomerId() != null) {
                CustomerDTO customerDTO = new CustomerDTO(null, customer.getCustomerId(), customer.getFullName(), customer.getEmail(), customer.getDob(), customer.getImage(), customer.getPhone(), true, true, partner.get().getId(), null);
                long count = partner.get().getCustomerList().stream().filter(p -> p.getCustomerId().equals(customer.getCustomerId()) && p.getStatus().equals(true)).count();
                if (count == 0) {
                    CustomerMembershipDTO customerMember = new CustomerMembershipDTO();
                    //Create Customer
                    Customer customerEntity = customerRepository.save(CustomerMapper.INSTANCE.toEntity(customerDTO));
                    customerEntity.getPartner().setFullName(partner.get().getFullName());

                    //Create Customer Membership DTO
                    customerMember.setCustomer(CustomerMapper.INSTANCE.toDTO(customerEntity));
                    customerMember.setMembership(null);
                    customerMember.setWalletList(null);
                    customerMember.setNextLevel(null);
                    customerMember.setLevelList(null);
                    return customerMember;
                } else {
                    throw new InvalidParameterException("Customer already exists");
                }
            } else {
                throw new InvalidParameterException("Customer id cannot be blank");
            }
        } else {
            throw new InvalidParameterException("Invalid partner");
        }
    }

    @Override
    public CustomerMembershipDTO createCustomerMembershipWeb(String token, CustomerProgramWebDTO customer) {
        String userName;
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Expired JWT token");
        }
        Optional<Partner> partner = partnerRepository.findPartnerByUserNameAndStatus(userName, true);
        if (partner.isPresent()) {
            String linkImg = null;
            if (customer.getImage() != null) {
                try {
                    linkImg = fileService.upload(customer.getImage());
                } catch (Exception e) {
                    throw new InvalidParameterException("Invalid image file !");
                }
            }
            if (customer.getCustomerId() != null) {
                CustomerDTO customerDTO = new CustomerDTO(null, customer.getCustomerId(), customer.getFullName(), customer.getEmail(), customer.getDob(), linkImg, customer.getPhone(), true, true, partner.get().getId(), null);
                long count = partner.get().getCustomerList().stream().filter(p -> p.getCustomerId().equals(customer.getCustomerId()) && p.getStatus().equals(true)).count();
                Optional<Program> program = partner.get().getProgramList().stream().filter(p -> p.getDateUpdated().isAfter(LocalDate.now().minusDays(1)) && p.getStatus().equals(true) && p.getState().equals(true)).findFirst();
                if (program.isPresent()) {
                    //Get Level
                    Optional<Level> level = program.get().getProgramLevelList().stream().map(ProgramLevel::getLevel).filter(l -> l.getCondition().compareTo(BigDecimal.ZERO) == 0).findFirst();
                    if (count == 0) {
                        if (level.isPresent()) {
                            CustomerMembershipDTO customerMember = new CustomerMembershipDTO();
                            //Create Customer
                            Customer customerEntity = customerRepository.save(CustomerMapper.INSTANCE.toEntity(customerDTO));
                            customerEntity.getPartner().setFullName(program.get().getPartner().getFullName());

                            //Create Membership
                            Membership membershipEntity = membershipRepository.save(MembershipMapper.INSTANCE.toEntity(new MembershipDTO(null, LocalDate.now(), BigDecimal.valueOf(0L), BigDecimal.valueOf(0L), true, true, level.get().getId(), level.get().getLevel(), customerEntity.getId(), null, program.get().getId(), program.get().getProgramName())));
                            membershipEntity.getLevel().setLevel(level.get().getLevel());
                            membershipEntity.getCustomer().setFullName(customer.getFullName());
                            membershipEntity.getProgram().setProgramName(program.get().getProgramName());

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
                        } else {
                            throw new InvalidParameterException("Has not found a valid level");
                        }
                    } else {
                        throw new InvalidParameterException("Customer already exists");
                    }
                } else {
                    throw new InvalidParameterException("Not found valid program");
                }
            } else {
                throw new InvalidParameterException("Customer id cannot be blank");
            }
        } else {
            throw new InvalidParameterException("Invalid partner");
        }
    }

    @Override
    public CustomerMembershipDTO createCustomerMembership(String token, CustomerProgramDTO customer) {
        String userName;
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Expired JWT token");
        }
        Optional<Partner> partner = partnerRepository.findPartnerByUserNameAndStatus(userName, true);
        if (partner.isPresent()) {
            if (customer.getCustomerId() != null) {
                CustomerDTO customerDTO = new CustomerDTO(null, customer.getCustomerId(), customer.getFullName(), customer.getEmail(), customer.getDob(), customer.getImage(), customer.getPhone(), true, true, partner.get().getId(), null);
                long count = partner.get().getCustomerList().stream().filter(p -> p.getCustomerId().equals(customer.getCustomerId()) && p.getStatus().equals(true)).count();
                Optional<Program> program = partner.get().getProgramList().stream().filter(p -> p.getDateUpdated().isAfter(LocalDate.now().minusDays(1)) && p.getStatus().equals(true) && p.getState().equals(true)).findFirst();
                if (program.isPresent()) {
                    //Get Level
                    Optional<Level> level = program.get().getProgramLevelList().stream().map(ProgramLevel::getLevel).filter(l -> l.getCondition().compareTo(BigDecimal.ZERO) == 0).findFirst();
                    if (count == 0) {
                        if (level.isPresent()) {
                            CustomerMembershipDTO customerMember = new CustomerMembershipDTO();
                            //Create Customer
                            Customer customerEntity = customerRepository.save(CustomerMapper.INSTANCE.toEntity(customerDTO));
                            customerEntity.getPartner().setFullName(program.get().getPartner().getFullName());

                            //Create Membership
                            Membership membershipEntity = membershipRepository.save(MembershipMapper.INSTANCE.toEntity(new MembershipDTO(null, LocalDate.now(), BigDecimal.valueOf(0L), BigDecimal.valueOf(0L), true, true, level.get().getId(), level.get().getLevel(), customerEntity.getId(), null, program.get().getId(), program.get().getProgramName())));
                            membershipEntity.getLevel().setLevel(level.get().getLevel());
                            membershipEntity.getCustomer().setFullName(customer.getFullName());
                            membershipEntity.getProgram().setProgramName(program.get().getProgramName());

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
                        } else {
                            throw new InvalidParameterException("Has not found a valid level");
                        }
                    } else {
                        throw new InvalidParameterException("Customer already exists");
                    }
                } else {
                    throw new InvalidParameterException("Not found valid program");
                }
            } else {
                throw new InvalidParameterException("Customer id cannot be blank");
            }
        } else {
            throw new InvalidParameterException("Invalid partner");
        }
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

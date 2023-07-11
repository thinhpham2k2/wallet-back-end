package com.wallet.service;

import com.wallet.dto.CustomerDTO;
import com.wallet.dto.CustomerExtraDTO;
import com.wallet.dto.CustomerUpdateDTO;
import com.wallet.dto.TitleDTO;
import com.wallet.entity.Customer;
import com.wallet.entity.Membership;
import com.wallet.entity.Partner;
import com.wallet.entity.Program;
import com.wallet.jwt.JwtTokenProvider;
import com.wallet.mapper.CustomerMapper;
import com.wallet.mapper.MembershipMapper;
import com.wallet.mapper.ProgramMapper;
import com.wallet.repository.*;
import com.wallet.service.interfaces.ICustomerService;
import com.wallet.service.interfaces.IPagingService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService implements ICustomerService {

    private final CustomerRepository customerRepository;

    private final PartnerRepository partnerRepository;

    private final MembershipRepository membershipRepository;

    private final TransactionRepository transactionRepository;

    private final ProgramRepository programRepository;

    private final IPagingService pagingService;

    @Override
    public Page<CustomerDTO> getCustomerList(boolean status, List<Long> partnerId, String search, String sort, int page, int limit) {
        if (limit < 1) throw new InvalidParameterException("Page size must not be less than one!");
        if (page < 0) throw new InvalidParameterException("Page number must not be less than zero!");
        List<Sort.Order> order = new ArrayList<>();
        Set<String> sourceFieldList = pagingService.getAllFields(Customer.class);
        String[] subSort = sort.split(",");
        if (pagingService.checkPropertPresent(sourceFieldList, subSort[0])) {
            order.add(new Sort.Order(pagingService.getSortDirection(subSort[1]), transferProperty(subSort[0])));
        } else {
            throw new InvalidParameterException(subSort[0] + " is not a propertied of Customer!");
        }
        Pageable pageable = PageRequest.of(page, limit).withSort(Sort.by(order));
        Page<Customer> pageResult = customerRepository.getCustomerList(true, partnerId, search, pageable);
        return new PageImpl<>(pageResult.getContent().stream().map(CustomerMapper.INSTANCE::toDTO).collect(Collectors.toList()), pageResult.getPageable(), pageResult.getTotalElements());
    }

    @Override
    public Page<CustomerDTO> getCustomerListForPartner(boolean status, String token, String search, String sort, int page, int limit) {
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
        Set<String> sourceFieldList = pagingService.getAllFields(Customer.class);
        String[] subSort = sort.split(",");
        if (pagingService.checkPropertPresent(sourceFieldList, subSort[0])) {
            order.add(new Sort.Order(pagingService.getSortDirection(subSort[1]), transferProperty(subSort[0])));
        } else {
            throw new InvalidParameterException(subSort[0] + " is not a propertied of Customer!");
        }
        Pageable pageable = PageRequest.of(page, limit).withSort(Sort.by(order));
        Page<Customer> pageResult = customerRepository.getCustomerListForPartner(true, userName, search, pageable);
        return new PageImpl<>(pageResult.getContent().stream().map(CustomerMapper.INSTANCE::toDTO).collect(Collectors.toList()), pageResult.getPageable(), pageResult.getTotalElements());
    }

    private static String transferProperty(String property) {
        if (property.equals("partner")) {
            return "partner.fullName";
        }
        return property;
    }

    @Override
    public CustomerExtraDTO getCustomerById(String token, long id, boolean isAdmin) {
        Optional<Customer> customer;
        if (isAdmin) {
            customer = customerRepository.findByStatusAndId(true, id);
        } else {
            String userName;
            try {
                JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
                userName = jwtTokenProvider.getUserNameFromJWT(token);
            } catch (ExpiredJwtException e) {
                throw new InvalidParameterException("Invalid JWT token");
            }
            customer = customerRepository.findByStatusAndId(true, id, userName);
        }
        if (customer.isPresent()) {
            CustomerExtraDTO customerExtra = new CustomerExtraDTO();
            customerExtra.setCustomer(CustomerMapper.INSTANCE.toDTO(customer.get()));
            List<Membership> memberships = customer.get().getMembershipList();
            if (!memberships.isEmpty()) {
                customerExtra.setMembershipList(memberships.stream().filter(m -> m.getStatus().equals(true)).map(MembershipMapper.INSTANCE::toDTO).collect(Collectors.toList()));
            }
            return customerExtra;
        }
        return null;
    }

    @Override
    public CustomerExtraDTO updateCustomer(CustomerUpdateDTO customerUpdate, long customerId, String token) {
        String userName;
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Invalid JWT token");
        }
        Optional<Customer> customer = customerRepository.findByStatusAndId(true, customerId, userName);

        if (customer.isPresent()) {
            if (customerUpdate.getFullName().isBlank()) {
                throw new InvalidParameterException("Full name is required");
            }

            try {
                customer.get().setDob(customerUpdate.getDob());
            } catch (Exception e) {
                throw new InvalidParameterException("Invalid date of birth");
            }

            customer.get().setFullName(customerUpdate.getFullName());
            customer.get().setEmail(customerUpdate.getEmail());
            customer.get().setImage(customerUpdate.getImage());
            customer.get().setPhone(customerUpdate.getPhone());
            customer.get().setState(customerUpdate.getState());

            Customer customer1 = customerRepository.save(customer.get());
            CustomerExtraDTO customerExtraDTO = new CustomerExtraDTO();
            customerExtraDTO.setCustomer(CustomerMapper.INSTANCE.toDTO(customer1));
            List<Membership> memberships = customer1.getMembershipList();
            if (!memberships.isEmpty()) {
                customerExtraDTO.setMembershipList(memberships.stream().filter(m -> m.getStatus().equals(true)).map(MembershipMapper.INSTANCE::toDTO).collect(Collectors.toList()));
            }
            return customerExtraDTO;
        } else {
            throw new InvalidParameterException("Not found customer");
        }
    }

    @Override
    public CustomerExtraDTO deleteCustomer(long customerId, String token) {
        String userName;
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Invalid JWT token");
        }
        Optional<Customer> customer = customerRepository.findByStatusAndId(true, customerId, userName);

        if (customer.isPresent()) {
            customer.get().setState(false);
            customer.get().setStatus(false);
            Customer customer1 = customerRepository.save(customer.get());
            CustomerExtraDTO customerExtraDTO = new CustomerExtraDTO();
            customerExtraDTO.setCustomer(CustomerMapper.INSTANCE.toDTO(customer1));
            List<Membership> memberships = customer1.getMembershipList();
            if (!memberships.isEmpty()) {
                customerExtraDTO.setMembershipList(memberships.stream().filter(m -> m.getStatus().equals(true)).map(MembershipMapper.INSTANCE::toDTO).collect(Collectors.toList()));
            }
            return customerExtraDTO;
        } else {
            throw new InvalidParameterException("Not found customer");
        }
    }

    @Override
    public TitleDTO getTitle(String token) {
        String userName;
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Invalid JWT token");
        }
        Optional<Partner> partner = partnerRepository.findPartnerByUserNameAndStatus(userName, true);
        if (partner.isPresent()) {
            TitleDTO title = new TitleDTO();
            title.setNumberOfCustomer(partner.get().getCustomerList().stream().filter(c -> c.getStatus().equals(true)).count());
            title.setNumberOfMember(membershipRepository.countAllByStatusAndPartner(true, partner.get().getId()));
            title.setNumberOfTransaction(transactionRepository.countAllByStatusAndPartner(true, partner.get().getId()));
            Optional<Program> programOptional = programRepository.getProgramByStatusAndStateAndPartnerId(true, true, partner.get().getId());
            title.setProgram(ProgramMapper.INSTANCE.toDTO(programOptional.orElseGet(Program::new)));
            return title;
        } else {
            throw new InvalidParameterException("Not found partner");
        }
    }
}

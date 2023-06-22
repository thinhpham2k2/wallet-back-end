package com.wallet.service;

import com.wallet.dto.CustomerDTO;
import com.wallet.dto.CustomerExtraDTO;
import com.wallet.entity.Customer;
import com.wallet.entity.Membership;
import com.wallet.jwt.JwtTokenProvider;
import com.wallet.mapper.CustomerMapper;
import com.wallet.mapper.MembershipMapper;
import com.wallet.repository.CustomerRepository;
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
}

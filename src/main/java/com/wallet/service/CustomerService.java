package com.wallet.service;

import com.wallet.dto.CustomerDTO;
import com.wallet.entity.Customer;
import com.wallet.mapper.CustomerMapper;
import com.wallet.repository.CustomerRepository;
import com.wallet.service.interfaces.ICustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService implements ICustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Page<CustomerDTO> getCustomerList(boolean status, List<Long> partnerId, String search, String sort, int page, int limit) {
        if(limit < 1)  throw new InvalidParameterException("Page size must not be less than one!");
        if(page < 0)  throw new InvalidParameterException("Page number must not be less than zero!");
        List<Sort.Order> order = new ArrayList<>();
        Set<String> sourceFieldList = getAllFields(Customer.class);
        String[] subSort = sort.split(",");
        if(ifPropertpresent(sourceFieldList, subSort[0])) {
            order.add(new Sort.Order(getSortDirection(subSort[1]), transferProperty(subSort[0])));
        } else {
            throw new InvalidParameterException(subSort[0] + " is not a propertied of Customer!");
        }
        Pageable pageable = PageRequest.of(page, limit).withSort(Sort.by(order));
        Page<Customer> pageResult = customerRepository.getCustomerList(true, partnerId, search, pageable);
        return new PageImpl<>(pageResult.getContent().stream().map(CustomerMapper.INSTANCE::toDTO).collect(Collectors.toList()), pageResult.getPageable(), pageResult.getTotalElements());
    }

    private static Set<String> getAllFields(final Class<?> type) {
        Set<String> fields = new HashSet<>();
        //loop the fields using Java Reflections
        for (Field field : type.getDeclaredFields()) {
            fields.add(field.getName());
        }
        //recursive call to getAllFields
        if (type.getSuperclass() != null) {
            fields.addAll(getAllFields(type.getSuperclass()));
        }
        return fields;
    }

    private static boolean ifPropertpresent(final Set<String> properties, final String propertyName) {
        return properties.contains(propertyName);
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    private static String transferProperty(String property){
        if (property.equals("partner")) {
            return "partner.fullName";
        }
        return property;
    }

}

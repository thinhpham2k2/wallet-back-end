package com.wallet.service;

import com.wallet.dto.MembershipDTO;
import com.wallet.entity.Membership;
import com.wallet.mapper.MembershipMapper;
import com.wallet.repository.MembershipRepository;
import com.wallet.service.interfaces.IMembershipService;
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
public class MembershipService implements IMembershipService {

    private final MembershipRepository membershipRepository;

    @Override
    public Page<MembershipDTO> getMemberList(boolean status, List<Long> partnerId, List<Long> programId, String search, String sort, int page, int limit) {
        if(limit < 1)  throw new InvalidParameterException("Page size must not be less than one!");
        if(page < 0)  throw new InvalidParameterException("Page number must not be less than zero!");
        List<Sort.Order> order = new ArrayList<>();
        Set<String> sourceFieldList = getAllFields(Membership.class);
        String[] subSort = sort.split(",");
        if(ifPropertpresent(sourceFieldList, subSort[0])) {
            order.add(new Sort.Order(getSortDirection(subSort[1]), transferProperty(subSort[0])));
        } else {
            throw new InvalidParameterException(subSort[0] + " is not a propertied of Membership!");
        }
        Pageable pageable = PageRequest.of(page, limit).withSort(Sort.by(order));
        Page<Membership> pageResult = membershipRepository.getMemberList(true, partnerId, programId, search, pageable);
        return new PageImpl<>(pageResult.getContent().stream().map(MembershipMapper.INSTANCE::toDTO).collect(Collectors.toList()), pageResult.getPageable(), pageResult.getTotalElements());
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

    private static String transferProperty(String property) {
        return switch (property) {
            case "level" -> "level.condition";
            case "customer" -> "customer.fullName";
            case "program" -> "program.programName";
            case "partner" -> "program.partner.fullName";
            default -> property;
        };
    }
}

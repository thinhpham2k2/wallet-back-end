package com.wallet.service;

import com.wallet.dto.ProgramDTO;
import com.wallet.entity.Program;
import com.wallet.mapper.ProgramMapper;
import com.wallet.repository.ProgramRepository;
import com.wallet.service.interfaces.IPagingService;
import com.wallet.service.interfaces.IProgramService;
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
public class ProgramService implements IProgramService {

    private final IPagingService pagingService;

    private final ProgramRepository programRepository;

    @Override
    public Page<ProgramDTO> getProgramList(boolean status, List<Long> partnerId, String search, String sort, int page, int limit) {
        if(limit < 1)  throw new InvalidParameterException("Page size must not be less than one!");
        if(page < 0)  throw new InvalidParameterException("Page number must not be less than zero!");
        List<Sort.Order> order = new ArrayList<>();
        Set<String> sourceFieldList = pagingService.getAllFields(Program.class);
        String[] subSort = sort.split(",");
        if(pagingService.checkPropertPresent(sourceFieldList, subSort[0])) {
            order.add(new Sort.Order(pagingService.getSortDirection(subSort[1]), transferProperty(subSort[0])));
        } else {
            throw new InvalidParameterException(subSort[0] + " is not a propertied of Program!");
        }
        Pageable pageable = PageRequest.of(page, limit).withSort(Sort.by(order));
        Page<Program> pageResult = programRepository.getProgramList(true, partnerId, search, pageable);
        return new PageImpl<>(pageResult.getContent().stream().map(ProgramMapper.INSTANCE::toDTO).collect(Collectors.toList()), pageResult.getPageable(), pageResult.getTotalElements());
    }

    private static String transferProperty(String property){
        if (property.equals("partner")) {
            return "partner.fullName";
        }
        return property;
    }
}

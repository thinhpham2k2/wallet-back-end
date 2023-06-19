package com.wallet.service;

import com.wallet.dto.LevelDTO;
import com.wallet.entity.Level;
import com.wallet.mapper.LevelMapper;
import com.wallet.repository.LevelRepository;
import com.wallet.service.interfaces.ILevelService;
import com.wallet.service.interfaces.IPagingService;
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
public class LevelService implements ILevelService {

    private final LevelRepository levelRepository;

    private final IPagingService pagingService;

    @Override
    public Page<LevelDTO> getLevelList(String sort, int page, int limit) {
        if(limit < 1)  throw new InvalidParameterException("Page size must not be less than one!");
        if(page < 0)  throw new InvalidParameterException("Page number must not be less than zero!");
        List<Sort.Order> order = new ArrayList<>();
        Set<String> sourceFieldList = pagingService.getAllFields(Level.class);
        String[] subSort = sort.split(",");
        if(pagingService.checkPropertPresent(sourceFieldList, subSort[0])) {
            order.add(new Sort.Order(pagingService.getSortDirection(subSort[1]), subSort[0]));
        } else {
            throw new InvalidParameterException(subSort[0] + " is not a propertied of Partner!");
        }
        Pageable pageable = PageRequest.of(page, limit).withSort(Sort.by(order));
        Page<Level> pageResult = levelRepository.findAllByStatus(true, pageable);
        return new PageImpl<>(pageResult.getContent().stream().map(LevelMapper.INSTANCE::toDTO).collect(Collectors.toList()), pageResult.getPageable(), pageResult.getTotalElements());
    }
}

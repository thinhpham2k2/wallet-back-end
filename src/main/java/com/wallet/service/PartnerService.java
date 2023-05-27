package com.wallet.service;

import com.wallet.dto.PartnerDTO;
import com.wallet.entity.Partner;
import com.wallet.mapper.PartnerMapper;
import com.wallet.repository.PartnerRepository;
import com.wallet.service.interfaces.IPartnerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PartnerService implements IPartnerService {

    private final PartnerRepository partnerRepository;

    @Override
    public PartnerDTO getByUsernameAndStatus(String userName, boolean status) {
        return PartnerMapper.INSTANCE.toDTO(partnerRepository.findPartnerByUserNameAndStatus(userName, status).get());
    }

    @Override
    public Page<PartnerDTO> getAllPartner(boolean status) {
        Pageable pageable = PageRequest.of(0, 10).withSort(Sort.by("userName"));

        Page<Partner> pageResult = partnerRepository.findPartnersByStatus(true, pageable);

        return new PageImpl<>(pageResult.getContent().stream().map(PartnerMapper.INSTANCE::toDTO).collect(Collectors.toList()),
                pageResult.getPageable(),
                pageResult.getTotalElements());
    }
}

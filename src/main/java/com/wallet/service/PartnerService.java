package com.wallet.service;

import com.wallet.dto.PartnerDTO;
import com.wallet.mapper.PartnerMapper;
import com.wallet.repository.PartnerRepository;
import com.wallet.service.interfaces.IPartnerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
    public List<PartnerDTO> getAllPartner(boolean status) {
        return partnerRepository.findPartnersByStatus(true).stream().map(PartnerMapper.INSTANCE::toDTO).collect(Collectors.toList());
    }
}
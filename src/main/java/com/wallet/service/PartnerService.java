package com.wallet.service;

import com.wallet.dto.JwtResponseDTO;
import com.wallet.dto.PartnerDTO;
import com.wallet.dto.PartnerRegisterDTO;
import com.wallet.entity.CustomUserDetails;
import com.wallet.entity.Partner;
import com.wallet.exception.PartnerException;
import com.wallet.exception.dto.PartnerErrorDTO;
import com.wallet.jwt.JwtTokenProvider;
import com.wallet.mapper.PartnerMapper;
import com.wallet.repository.AdminRepository;
import com.wallet.repository.PartnerRepository;
import com.wallet.service.interfaces.IPartnerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PartnerService implements IPartnerService {

    private final PartnerRepository partnerRepository;

    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public PartnerDTO getByUsernameAndStatus(String userName, boolean status) {
        return PartnerMapper.INSTANCE.toDTO(partnerRepository.findPartnerByUserNameAndStatus(userName, status).get());
    }

    @Override
    public Page<PartnerDTO> getAllPartner(boolean status) {
        Pageable pageable = PageRequest.of(0, 10).withSort(Sort.by("userName"));

        Page<Partner> pageResult = partnerRepository.findPartnersByStatus(true, pageable);

        return new PageImpl<>(pageResult.getContent().stream().map(PartnerMapper.INSTANCE::toDTO).collect(Collectors.toList()), pageResult.getPageable(), pageResult.getTotalElements());
    }

    @Override
    public JwtResponseDTO creatPartner(PartnerRegisterDTO partnerRegisterDTO, Long jwtExpiration) {
        boolean flag = false;
        PartnerDTO partnerDTO = partnerRegisterDTO.getPartnerDTO();
        PartnerErrorDTO partnerErrorDTO = new PartnerErrorDTO();

        //Validate User Name
        if (!partnerDTO.getUserName().isBlank()) {
            if (partnerRepository.existsPartnerByUserName(partnerDTO.getUserName()) || adminRepository.existsAdminByUserName(partnerDTO.getUserName())) {
                flag = true;
                partnerErrorDTO.setUserName("Used user name !");
            }
        } else {
            flag = true;
            partnerErrorDTO.setUserName("User name mustn't be blank!");
        }

        //Validate Email
        if (!partnerDTO.getEmail().isBlank()) {
            if (partnerRepository.existsPartnerByEmail(partnerDTO.getEmail()) || adminRepository.existsAdminByEmail(partnerDTO.getEmail())) {
                flag = true;
                partnerErrorDTO.setEmail("Used email !");
            }
        } else {
            flag = true;
            partnerErrorDTO.setEmail("Email mustn't be blank !");
        }

        //Validate Code
        if (!partnerDTO.getCode().isBlank()) {
            if (partnerRepository.existsPartnerByCode(partnerDTO.getCode())) {
                flag = true;
                partnerErrorDTO.setCode("Used code !");
            }
        } else {
            flag = true;
            partnerErrorDTO.setCode("Code mustn't be blank !");
        }

        //Validate Full name
        if (partnerDTO.getFullName().isBlank()) {
            flag = true;
            partnerErrorDTO.setFullName("Full name mustn't be blank !");
        }

        //Validate Password
        if (partnerRegisterDTO.getPassword().isBlank()) {
            flag = true;
            partnerErrorDTO.setPassword("Password mustn't be blank !");
        } else if (partnerRegisterDTO.getPassword().length() < 8) {
            flag = true;
            partnerErrorDTO.setPassword("The password must be 8 characters or more !");
        }

        if (flag) {
            throw new PartnerException(partnerErrorDTO);
        } else {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            Partner partner = PartnerMapper.INSTANCE.toEntity(partnerDTO);
            partner.setId(null);
            partner.setState(true);
            partner.setStatus(true);
            partner.setPassword(passwordEncoder.encode(partnerRegisterDTO.getPassword()));
            PartnerDTO partnerRegister = PartnerMapper.INSTANCE.toDTO(partnerRepository.save(partner));
            return new JwtResponseDTO(jwtTokenProvider.generateToken((CustomUserDetails) customUserDetailsService.loadUserByPartner(partnerRegister), jwtExpiration), partnerRegister, null);
        }
    }

    @Override
    public PartnerDTO getByIdAndStatus(Long id, boolean status) {
        Optional<Partner> partner = partnerRepository.findPartnerByIdAndStatus(id, status);
        return partner.map(PartnerMapper.INSTANCE::toDTO).orElse(null);
    }
}

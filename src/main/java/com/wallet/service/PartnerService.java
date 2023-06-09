package com.wallet.service;

import com.wallet.dto.*;
import com.wallet.entity.CustomUserDetails;
import com.wallet.entity.Partner;
import com.wallet.exception.PartnerException;
import com.wallet.exception.dto.PartnerErrorDTO;
import com.wallet.exception.dto.PartnerErrorUpdateDTO;
import com.wallet.jwt.JwtTokenProvider;
import com.wallet.mapper.PartnerMapper;
import com.wallet.mapper.PartnerRegisterMapper;
import com.wallet.mapper.ProgramMapper;
import com.wallet.repository.AdminRepository;
import com.wallet.repository.CustomerRepository;
import com.wallet.repository.PartnerRepository;
import com.wallet.service.interfaces.IFileService;
import com.wallet.service.interfaces.IPagingService;
import com.wallet.service.interfaces.IPartnerService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class PartnerService implements IPartnerService {

    private final PartnerRepository partnerRepository;

    private final AdminRepository adminRepository;

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    private final CustomUserDetailsService customUserDetailsService;

    private final IPagingService pagingService;

    private final IFileService fileService;

    @Override
    public PartnerDTO getByUsernameAndStatus(String userName, boolean status) {
        return PartnerMapper.INSTANCE.toDTO(partnerRepository.findPartnerByUserNameAndStatus(userName, status).get());
    }

    @Override
    public Page<PartnerDTO> getPartnerList(boolean status, String search, String sort, int page, int limit) {
        if (limit < 1) throw new InvalidParameterException("Page size must not be less than one!");
        if (page < 0) throw new InvalidParameterException("Page number must not be less than zero!");
        List<Sort.Order> order = new ArrayList<>();
        Set<String> sourceFieldList = pagingService.getAllFields(Partner.class);
        String[] subSort = sort.split(",");
        if (pagingService.checkPropertPresent(sourceFieldList, subSort[0])) {
            order.add(new Sort.Order(pagingService.getSortDirection(subSort[1]), subSort[0]));
        } else {
            throw new InvalidParameterException(subSort[0] + " is not a propertied of Partner!");
        }
        Pageable pageable = PageRequest.of(page, limit).withSort(Sort.by(order));
        Page<Partner> pageResult = partnerRepository.getPartnerList(true, search, pageable);
        return new PageImpl<>(pageResult.getContent().stream().map(PartnerMapper.INSTANCE::toDTO).collect(Collectors.toList()), pageResult.getPageable(), pageResult.getTotalElements());
    }


    @Override
    public JwtResponseDTO creatPartner(PartnerRegisterDTO partnerRegisterDTO, Long jwtExpiration) {
        boolean flag = false;
        PartnerErrorDTO partnerErrorDTO = new PartnerErrorDTO();

        //Validate User Name
        if (!partnerRegisterDTO.getUserName().isBlank()) {
            if (partnerRepository.existsPartnerByUserName(partnerRegisterDTO.getUserName()) || adminRepository.existsAdminByUserName(partnerRegisterDTO.getUserName())) {
                flag = true;
                partnerErrorDTO.setUserName("Used user name !");
            }
        } else {
            flag = true;
            partnerErrorDTO.setUserName("User name mustn't be blank!");
        }

        //Validate Email
        if (!partnerRegisterDTO.getEmail().isBlank()) {
            if (partnerRepository.existsPartnerByEmail(partnerRegisterDTO.getEmail()) || adminRepository.existsAdminByEmail(partnerRegisterDTO.getEmail())) {
                flag = true;
                partnerErrorDTO.setEmail("Used email !");
            }
        } else {
            flag = true;
            partnerErrorDTO.setEmail("Email mustn't be blank !");
        }

        //Validate Code
        if (!partnerRegisterDTO.getCode().isBlank()) {
            if (partnerRepository.existsPartnerByCode(partnerRegisterDTO.getCode())) {
                flag = true;
                partnerErrorDTO.setCode("Used code !");
            }
        } else {
            flag = true;
            partnerErrorDTO.setCode("Code mustn't be blank !");
        }

        //Validate Full name
        if (partnerRegisterDTO.getFullName().isBlank()) {
            flag = true;
            partnerErrorDTO.setFullName("Full name mustn't be blank !");
        }

        //Validate Phone
        if (partnerRegisterDTO.getPhone().length() > 17) {
            flag = true;
            partnerErrorDTO.setPhone("Phone number length must be 17 characters or less !");
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
            throw new PartnerException(partnerErrorDTO, null);
        } else {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            Partner partner = PartnerRegisterMapper.INSTANCE.toEntity(partnerRegisterDTO);
            partner.setId(null);
            partner.setState(true);
            partner.setStatus(true);
            partner.setImage("");
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

    @Override
    public PartnerExtraDTO getPartnerExtra(Long id, boolean status) {
        Optional<Partner> partner = partnerRepository.findPartnerByIdAndStatus(id, status);
        PartnerExtraDTO partnerExtra = new PartnerExtraDTO();
        if (partner.isPresent()) {
            partnerExtra.setNumOfCustomers(customerRepository.countAllByStatusAndPartnerId(true, id));
            partnerExtra.setPartner(PartnerMapper.INSTANCE.toDTO(partner.get()));
            partnerExtra.setProgramList(partner.get().getProgramList().stream().filter(p -> p.getStatus().equals(true)).map(ProgramMapper.INSTANCE::toDTO).collect(Collectors.toList()));
            return partnerExtra;
        }
        return null;
    }

    @Override
    public PartnerDTO updatePartner(PartnerUpdateDTO partnerDTO, String token) {
        boolean flag = false;
        PartnerErrorUpdateDTO partnerErrorDTO = new PartnerErrorUpdateDTO();
        String userName;
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Invalid JWT token");
        }
        Optional<Partner> partner = partnerRepository.getPartnerByUserNameAndStatus(userName, true);
        if (partner.isPresent()) {
            //Validate Full name
            if (partnerDTO.getFullName().isBlank()) {
                flag = true;
                partnerErrorDTO.setFullName("Full name mustn't be blank !");
            }

            //Validate Phone
            if (partnerDTO.getPhone().length() > 17) {
                flag = true;
                partnerErrorDTO.setPhone("Phone number length must be 17 characters or less !");
            }

            //Validate State
            if (partnerDTO.getState() == null) {
                flag = true;
                partnerErrorDTO.setState("Invalid state !");
            }

            //Validate Image
            String linkImg = "";
            if (partnerDTO.getImage() == null) {
                linkImg = partner.get().getImage();
            } else {
                if (!flag) {
                    try {
                        linkImg = fileService.upload(partnerDTO.getImage());
                    } catch (Exception e) {
                        partnerErrorDTO.setImage("Invalid image file !");
                        throw new PartnerException(null, partnerErrorDTO);
                    }
                }
            }

            if (flag) {
                throw new PartnerException(null, partnerErrorDTO);
            } else {
                partner.get().setFullName(partnerDTO.getFullName());
                partner.get().setImage(linkImg);
                partner.get().setPhone(partnerDTO.getPhone());
                partner.get().setAddress(partnerDTO.getAddress());
                partner.get().setState(partnerDTO.getState());
                return PartnerMapper.INSTANCE.toDTO(partnerRepository.save(partner.get()));
            }
        } else {
            throw new InvalidParameterException("Invalid partner !");
        }
    }

    @Override
    public PartnerDTO deletePartner(Long id) {
        Optional<Partner> partnerOptional = partnerRepository.findPartnerByIdAndStatus(id, true);
        if (partnerOptional.isPresent()) {
            partnerOptional.get().setStatus(false);
            Partner partner = partnerRepository.save(partnerOptional.get());
            return PartnerMapper.INSTANCE.toDTO(partner);
        } else {
            throw new InvalidParameterException("Invalid partner !");
        }
    }

    @Override
    public PartnerDTO getPartnerProfile(String token) {
        String userName;
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Invalid JWT token");
        }
        Optional<Partner> partner = partnerRepository.getPartnerByUserNameAndStatus(userName, true);
        return partner.map(PartnerMapper.INSTANCE::toDTO).orElse(null);
    }
}

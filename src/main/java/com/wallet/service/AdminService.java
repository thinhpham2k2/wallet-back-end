package com.wallet.service;

import com.wallet.dto.AdminDTO;
import com.wallet.dto.AdminRegisterDTO;
import com.wallet.dto.AdminUpdateDTO;
import com.wallet.dto.JwtResponseDTO;
import com.wallet.entity.Admin;
import com.wallet.entity.CustomUserDetails;
import com.wallet.exception.AdminException;
import com.wallet.exception.dto.AdminErrorDTO;
import com.wallet.exception.dto.AdminErrorUpdateDTO;
import com.wallet.jwt.JwtTokenProvider;
import com.wallet.mapper.AdminMapper;
import com.wallet.mapper.AdminRegisterMapper;
import com.wallet.repository.AdminRepository;
import com.wallet.repository.PartnerRepository;
import com.wallet.service.interfaces.IAdminService;
import com.wallet.service.interfaces.IFileService;
import com.wallet.service.interfaces.IPagingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService implements IAdminService {

    private final AdminRepository adminRepository;

    private final PartnerRepository partnerRepository;

    private final PasswordEncoder passwordEncoder;

    private final CustomUserDetailsService customUserDetailsService;

    private final IPagingService pagingService;

    private final IFileService fileService;

    @Override
    public AdminDTO getByUsernameAndStatus(String userName, boolean status) {
        return AdminMapper.INSTANCE.toDTO(adminRepository.findAdminByUserNameAndStatus(userName, status).get());
    }

    @Override
    public AdminDTO getByIdAndStatus(Long id, boolean status) {
        Optional<Admin> admin = adminRepository.findAdminByIdAndStatus(id, status);
        return admin.map(AdminMapper.INSTANCE::toDTO).orElse(null);
    }

    @Override
    public Page<AdminDTO> getAllAdmin(boolean status, Integer page) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, 10).withSort(Sort.by("userName"));
        Page<Admin> pageResult = adminRepository.findAdminsByStatus(status, pageable);
        return new PageImpl<>(pageResult.getContent().stream().map(AdminMapper.INSTANCE::toDTO).collect(Collectors.toList()), pageResult.getPageable(), pageResult.getTotalElements());
    }

    @Override
    public AdminDTO deleteAdmin(Long id) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (id.equals(user.getAdmin().getId())) {
            throw new InvalidParameterException("Unable to delete the account in current !");
        }
        Optional<Admin> adminOptional = adminRepository.findAdminByIdAndStatus(id, true);
        if (adminOptional.isPresent()) {
            adminOptional.get().setStatus(false);
            Admin admin = adminRepository.save(adminOptional.get());
            return AdminMapper.INSTANCE.toDTO(admin);
        } else {
            throw new InvalidParameterException("Invalid admin !");
        }
    }

    @Override
    public AdminDTO updateAdmin(AdminUpdateDTO adminDTO, Long id) {
        boolean flag = false;
        AdminErrorUpdateDTO errorUpdateDTO = new AdminErrorUpdateDTO();

        //Validate Id
        if (id == null) {
            flag = true;
            errorUpdateDTO.setId("Admin Id mustn't be blank !");
        }

        //Validate Full name
        if (adminDTO.getFullName().isBlank()) {
            flag = true;
            errorUpdateDTO.setFullName("Full name mustn't be blank !");
        }

        //Validate date of birth
        if (adminDTO.getDob() == null) {
            flag = true;
            errorUpdateDTO.setDob("Date of birth mustn't be blank !");
        } else {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(adminDTO.getDob().toString(), formatter);
                if (date.isAfter(LocalDate.now().minusYears(18))) {
                    flag = true;
                    errorUpdateDTO.setDob("The date of birth must be over 18 years old !");
                }
            } catch (Exception e) {
                flag = true;
                errorUpdateDTO.setDob("Invalid date of birth !");
            }
        }

        //Validate Phone
        if (adminDTO.getPhone().length() > 17) {
            flag = true;
            errorUpdateDTO.setPhone("Phone number length must be 17 characters or less !");
        }
        if (adminRepository.existsAdminByPhoneAndIdNot(adminDTO.getPhone(), id)) {
            flag = true;
            errorUpdateDTO.setPhone("Duplicate phone number !");
        }

        //Validate Image
        String linkImg = "";
        if (!flag) {
            try {
                linkImg = fileService.upload(adminDTO.getImage());
            } catch (Exception e) {
                errorUpdateDTO.setImage("Invalid image file !");
                throw new AdminException(errorUpdateDTO, null);
            }
        }

        if (flag) {
            throw new AdminException(errorUpdateDTO, null);
        } else {
            Optional<Admin> adminOptional = adminRepository.findById(id);
            if (adminOptional.isPresent()) {
                adminOptional.get().setFullName(adminDTO.getFullName());
                adminOptional.get().setDob(adminDTO.getDob());
                adminOptional.get().setImage(linkImg);
                adminOptional.get().setPhone(adminDTO.getPhone());
                adminOptional.get().setStatus(adminDTO.getStatus());
                return AdminMapper.INSTANCE.toDTO(adminRepository.save(adminOptional.get()));
            } else {
                throw new InvalidParameterException("Invalid admin !");
            }
        }
    }

    @Override
    public JwtResponseDTO createAdmin(AdminRegisterDTO adminRegisterDTO, Long jwtExpiration) {
        boolean flag = false;
        AdminErrorDTO adminErrorDTO = new AdminErrorDTO();

        //Validate User Name
        if (!adminRegisterDTO.getUserName().isBlank()) {
            if (partnerRepository.existsPartnerByUserName(adminRegisterDTO.getUserName()) || adminRepository.existsAdminByUserName(adminRegisterDTO.getUserName())) {
                flag = true;
                adminErrorDTO.setUserName("Used user name !");
            }
        } else {
            flag = true;
            adminErrorDTO.setUserName("User name mustn't be blank!");
        }

        //Validate Email
        if (!adminRegisterDTO.getEmail().isBlank()) {
            if (partnerRepository.existsPartnerByEmail(adminRegisterDTO.getEmail()) || adminRepository.existsAdminByEmail(adminRegisterDTO.getEmail())) {
                flag = true;
                adminErrorDTO.setEmail("Used email !");
            }
        } else {
            flag = true;
            adminErrorDTO.setEmail("Email mustn't be blank !");
        }

        //Validate Full name
        if (adminRegisterDTO.getFullName().isBlank()) {
            flag = true;
            adminErrorDTO.setFullName("Full name mustn't be blank !");
        }

        //Validate Phone
        if (adminRegisterDTO.getPhone().length() > 17) {
            flag = true;
            adminErrorDTO.setPhone("Phone number length must be 17 characters or less !");
        }

        //Validate date of birth
        if (adminRegisterDTO.getDob() == null) {
            flag = true;
            adminErrorDTO.setDob("Date of birth mustn't be blank !");
        } else {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(adminRegisterDTO.getDob().toString(), formatter);
                if (date.isAfter(LocalDate.now().minusYears(18))) {
                    flag = true;
                    adminErrorDTO.setDob("The date of birth must be over 18 years old !");
                }
            } catch (Exception e) {
                flag = true;
                adminErrorDTO.setDob("Invalid date of birth !");
            }
        }

        //Validate Password
        if (adminRegisterDTO.getPassword().isBlank()) {
            flag = true;
            adminErrorDTO.setPassword("Password mustn't be blank !");
        } else if (adminRegisterDTO.getPassword().length() < 8) {
            flag = true;
            adminErrorDTO.setPassword("The password must be 8 characters or more !");
        }

        //Validate Image
        String linkImg = "";
        if (!flag) {
            try {
                linkImg = fileService.upload(adminRegisterDTO.getImage());
            } catch (Exception e) {
                adminErrorDTO.setImage("Invalid image file !");
                throw new AdminException(null, adminErrorDTO);
            }
        }

        if (flag) {
            throw new AdminException(null, adminErrorDTO);
        } else {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            Admin admin = AdminRegisterMapper.INSTANCE.toEntity(adminRegisterDTO);
            admin.setId(null);
            admin.setStatus(true);
            admin.setImage(linkImg);
            admin.setPassword(passwordEncoder.encode(adminRegisterDTO.getPassword()));
            AdminDTO adminDTO1 = AdminMapper.INSTANCE.toDTO(adminRepository.save(admin));
            return new JwtResponseDTO(jwtTokenProvider.generateToken((CustomUserDetails) customUserDetailsService.loadUserByAdmin(adminDTO1), jwtExpiration), null, adminDTO1);
        }
    }

    @Override
    public Page<AdminDTO> getAdminList(boolean status, String search, String sort, int page, int limit) {
        if (limit < 1) throw new InvalidParameterException("Page size must not be less than one!");
        if (page < 0) throw new InvalidParameterException("Page number must not be less than zero!");
        List<Sort.Order> order = new ArrayList<>();
        Set<String> sourceFieldList = pagingService.getAllFields(Admin.class);
        String[] subSort = sort.split(",");
        if (pagingService.checkPropertPresent(sourceFieldList, subSort[0])) {
            order.add(new Sort.Order(pagingService.getSortDirection(subSort[1]), subSort[0]));
        } else {
            throw new InvalidParameterException(subSort[0] + " is not a propertied of Partner!");
        }
        Pageable pageable = PageRequest.of(page, limit).withSort(Sort.by(order));
        Page<Admin> pageResult = adminRepository.getAdminList(true, search, pageable);
        return new PageImpl<>(pageResult.getContent().stream().map(AdminMapper.INSTANCE::toDTO).collect(Collectors.toList()), pageResult.getPageable(), pageResult.getTotalElements());
    }

}

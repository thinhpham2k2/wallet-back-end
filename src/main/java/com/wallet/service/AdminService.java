package com.wallet.service;

import com.wallet.dto.AdminDTO;
import com.wallet.mapper.AdminMapper;
import com.wallet.repository.AdminRepository;
import com.wallet.service.interfaces.IAdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService implements IAdminService {

    private final AdminRepository adminRepository;

    @Override
    public AdminDTO getByUsernameAndStatus(String userName, boolean status) {
        return AdminMapper.INSTANCE.toDTO(adminRepository.findAdminByUserNameAndStatus(userName, status).get());
    }
}

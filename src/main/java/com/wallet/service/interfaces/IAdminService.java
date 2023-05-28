package com.wallet.service.interfaces;

import com.wallet.dto.AdminDTO;
import com.wallet.dto.AdminRegisterDTO;
import com.wallet.dto.JwtResponseDTO;
import org.springframework.data.domain.Page;

public interface IAdminService {

    AdminDTO getByUsernameAndStatus(String userName, boolean status);

    AdminDTO getByIdAndStatus(Long id, boolean status);

    AdminDTO updateAdmin(AdminDTO adminDTO, Long id);

    AdminDTO deleteAdmin(Long id);

    Page<AdminDTO> getAllAdmin(boolean status, Integer page);

    JwtResponseDTO createAdmin(AdminRegisterDTO adminRegisterDTO, Long jwtExpiration);
}

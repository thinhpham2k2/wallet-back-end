package com.wallet.service.interfaces;

import com.wallet.dto.AdminDTO;
import com.wallet.dto.AdminRegisterDTO;
import com.wallet.dto.AdminUpdateDTO;
import com.wallet.dto.JwtResponseDTO;
import org.springframework.data.domain.Page;

public interface IAdminService {

    AdminDTO getByUsernameAndStatus(String userName, boolean status);

    AdminDTO getByIdAndStatus(Long id, boolean status);

    AdminDTO updateAdmin(AdminUpdateDTO adminDTO, Long id);

    AdminDTO deleteAdmin(Long id);

    Page<AdminDTO> getAllAdmin(boolean status, Integer page);

    Page<AdminDTO> getAdminList(boolean status, String search, String sort, int page, int limit);

    JwtResponseDTO createAdmin(AdminRegisterDTO adminRegisterDTO, Long jwtExpiration);
}

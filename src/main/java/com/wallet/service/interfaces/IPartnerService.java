package com.wallet.service.interfaces;

import com.wallet.dto.JwtResponseDTO;
import com.wallet.dto.PartnerDTO;
import com.wallet.dto.PartnerRegisterDTO;
import org.springframework.data.domain.Page;

public interface IPartnerService {

    PartnerDTO getByUsernameAndStatus(String userName, boolean status);

    PartnerDTO getByIdAndStatus(Long id, boolean status);

    PartnerDTO updatePartner(PartnerDTO partnerDTO, Long id);

    PartnerDTO deletePartner(Long id);

    Page<PartnerDTO> getAllPartner(boolean status, Integer page);

    JwtResponseDTO creatPartner(PartnerRegisterDTO partnerRegisterDTO, Long jwtExpiration);
}

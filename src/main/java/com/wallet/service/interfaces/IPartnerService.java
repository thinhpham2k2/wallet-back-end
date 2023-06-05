package com.wallet.service.interfaces;

import com.wallet.dto.JwtResponseDTO;
import com.wallet.dto.PartnerDTO;
import com.wallet.dto.PartnerRegisterDTO;
import com.wallet.dto.PartnerUpdateDTO;
import org.springframework.data.domain.Page;

public interface IPartnerService {

    PartnerDTO getByUsernameAndStatus(String userName, boolean status);

    PartnerDTO getByIdAndStatus(Long id, boolean status);

    PartnerUpdateDTO updatePartner(PartnerUpdateDTO partnerDTO, Long id);

    PartnerDTO deletePartner(Long id);

    Page<PartnerDTO> getPartnerList(boolean status, String search, String sort, int page, int limit);

    JwtResponseDTO creatPartner(PartnerRegisterDTO partnerRegisterDTO, Long jwtExpiration);
}

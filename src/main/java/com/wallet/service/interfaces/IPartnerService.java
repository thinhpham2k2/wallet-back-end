package com.wallet.service.interfaces;

import com.wallet.dto.*;
import com.wallet.entity.Partner;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IPartnerService {

    PartnerDTO getByUsernameAndStatus(String userName, boolean status);

    PartnerDTO getByIdAndStatus(Long id, boolean status);

    PartnerExtraDTO getPartnerExtra(Long id, boolean status);

    PartnerUpdateDTO updatePartner(PartnerUpdateDTO partnerDTO, Long id);

    PartnerDTO deletePartner(Long id);

    Page<PartnerDTO> getPartnerList(boolean status, String search, String sort, int page, int limit);

    JwtResponseDTO creatPartner(PartnerRegisterDTO partnerRegisterDTO, Long jwtExpiration);

    PartnerDTO getPartnerProfile(String token);
}

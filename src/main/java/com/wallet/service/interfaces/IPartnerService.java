package com.wallet.service.interfaces;

import com.wallet.dto.PartnerDTO;
import org.springframework.data.domain.Page;

public interface IPartnerService {

    PartnerDTO getByUsernameAndStatus(String userName, boolean status);

    Page<PartnerDTO> getAllPartner(boolean status);

    PartnerDTO creatPartner(PartnerDTO partner);
}

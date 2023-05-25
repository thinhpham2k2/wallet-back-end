package com.wallet.service.interfaces;

import com.wallet.dto.PartnerDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IPartnerService {

    PartnerDTO getByUsernameAndStatus(String userName, boolean status);

    Page<PartnerDTO> getAllPartner(boolean status);

}

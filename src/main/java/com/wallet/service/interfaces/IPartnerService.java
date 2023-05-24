package com.wallet.service.interfaces;

import com.wallet.dto.PartnerDTO;

import java.util.List;

public interface IPartnerService {

    PartnerDTO getByUsernameAndStatus(String userName, boolean status);

    List<PartnerDTO> getAllPartner(boolean status);

}

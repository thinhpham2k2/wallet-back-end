package com.wallet.service.interfaces;

import com.wallet.dto.PartnerDTO;

public interface IPartnerService {

    PartnerDTO getByUsernameAndStatus(String userName, boolean status);

}

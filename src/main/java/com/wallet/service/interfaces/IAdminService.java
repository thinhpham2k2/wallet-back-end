package com.wallet.service.interfaces;

import com.wallet.dto.AdminDTO;

public interface IAdminService {

    AdminDTO getByUsernameAndStatus(String userName, boolean status);

}

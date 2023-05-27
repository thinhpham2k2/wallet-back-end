package com.wallet.service.interfaces;

import com.wallet.dto.PartnerDTO;
import org.springframework.security.core.userdetails.UserDetails;

public interface ICustomUserDetailsService {

    UserDetails loadUserByEmail(String email);

    UserDetails loadUserByPartner(PartnerDTO partnerDTO);

}

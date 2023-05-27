package com.wallet.service.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface ICustomUserDetailsService {

    UserDetails loadUserByEmail(String email);

}

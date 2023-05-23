package com.wallet.service;

import com.wallet.entity.Admin;
import com.wallet.entity.CustomUserDetails;
import com.wallet.entity.Partner;
import com.wallet.repository.AdminRepository;
import com.wallet.repository.PartnerRepository;
import com.wallet.service.interfaces.ICustomUserDetailsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements ICustomUserDetailsService, UserDetailsService {

    private final AdminRepository adminRepository;

    private final PartnerRepository partnerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Partner> partner = partnerRepository.findPartnerByUserNameAndStatus(username, true);
        Optional<Admin> admin = adminRepository.findAdminByUserNameAndStatus(username, true);
        String role;

        if (partner.isEmpty()) {
            if (admin.isEmpty()) {
                throw new UsernameNotFoundException(username);
            } else {
                role = "Admin";
            }
        }else {
            role = "Partner";
        }

        Set<GrantedAuthority> authoritySet = new HashSet<>();
        authoritySet.add(new SimpleGrantedAuthority(role));

        return new CustomUserDetails(admin.isPresent() && !partner.isPresent() ? admin.get() : null, partner.isPresent() ? partner.get() : null, authoritySet, role);
    }

}

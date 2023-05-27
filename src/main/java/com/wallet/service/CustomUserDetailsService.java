package com.wallet.service;

import com.wallet.dto.PartnerDTO;
import com.wallet.entity.Admin;
import com.wallet.entity.CustomUserDetails;
import com.wallet.entity.Partner;
import com.wallet.mapper.PartnerMapper;
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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
        authoritySet.add(new SimpleGrantedAuthority("ROLE_"+role));

        return new CustomUserDetails(admin.isPresent() && partner.isEmpty() ? admin.get() : null, partner.orElse(null), authoritySet, role);
    }

    @Override
    public UserDetails loadUserByEmail(String email) {
        Optional<Partner> partner = partnerRepository.findPartnerByEmailAndStatus(email, true);
        Optional<Admin> admin = adminRepository.findAdminByEmailAndStatus(email, true);
        String role;

        if (partner.isEmpty()) {
            if (admin.isEmpty()) {
                throw new UsernameNotFoundException(email);
            } else {
                role = "Admin";
            }
        }else {
            role = "Partner";
        }

        Set<GrantedAuthority> authoritySet = new HashSet<>();
        authoritySet.add(new SimpleGrantedAuthority("ROLE_"+role));

        return new CustomUserDetails(admin.isPresent() && partner.isEmpty() ? admin.get() : null, partner.orElse(null), authoritySet, role);
    }

    @Override
    public UserDetails loadUserByPartner(PartnerDTO partnerDTO) {
        String role = "Partner";
        Set<GrantedAuthority> authoritySet = new HashSet<>();
        authoritySet.add(new SimpleGrantedAuthority("ROLE_"+role));

        return new CustomUserDetails(null, PartnerMapper.INSTANCE.toEntity(partnerDTO), authoritySet, role);
    }
}

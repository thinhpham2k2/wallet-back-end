package com.wallet.service;

import com.wallet.repository.AdminRepository;
import com.wallet.repository.PartnerRepository;
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
        if (userRepository.findByEmailAndStatus(username, true).isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        User user = userRepository.findByEmailAndStatus(username, true).get();
        Set<GrantedAuthority> authoritySet;
        authoritySet = new HashSet<>();
        String role = user.getRole().getRoleName();
        List<String> listAuth = generateAuthoriessByRoleId(user.getRole().getListRolePermissionScope());

        for (String s: listAuth) {
            authoritySet.add(new SimpleGrantedAuthority(s));
        }
        return new CustomUserDetails(user, authoritySet, role);
    }

    @Override
    public UserDetails loadUserById(Long id) throws Exception {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new Exception();
        }
        User result = user.get();
        String role = result.getRole().getRoleName();
        Set<GrantedAuthority> authoritySet;
        authoritySet = new HashSet<>();
        List<String> listAuth = generateAuthoriessByRoleId(result.getRole().getListRolePermissionScope());

        for (String s: listAuth) {
            authoritySet.add(new SimpleGrantedAuthority(s));
        }
        return new CustomUserDetails(result, authoritySet, role);
    }

    @Override
    public List<String> generateAuthoriessByRoleId(List<RolePermissionScope> rolePermissionScopeEntityList) {
        List<String> result = new ArrayList<>();
        for (RolePermissionScope item: rolePermissionScopeEntityList) {
            String s = "";
            s = "ROLE_"+  item.getPermission().getPermissionName() + "_"+item.getPermissionScope().getScopeName() ;
            result.add(s);
        }
        return result;
    }

}

package com.wallet.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {

    private Admin admin;

    private Partner partner;

    Set<GrantedAuthority> authoritySet;

    private String role;

    public String getRole(){
        return role;
    }

    public Admin getAdmin() {return admin;}

    public Partner getPartner(){ return partner; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authoritySet;
    }

    @Override
    public String getPassword() {
        if(this.partner == null){
            return admin.getPassword();
        }
        return partner.getPassword();
    }

    @Override
    public String getUsername() {
        if(this.partner == null){
            return admin.getUserName();
        }
        return partner.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}

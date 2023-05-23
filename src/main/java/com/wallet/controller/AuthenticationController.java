package com.wallet.controller;

import com.wallet.dto.AdminDTO;
import com.wallet.dto.JwtResponseDTO;
import com.wallet.dto.LoginFormDTO;
import com.wallet.dto.PartnerDTO;
import com.wallet.entity.CustomUserDetails;
import com.wallet.jwt.JwtTokenProvider;
import com.wallet.mapper.AdminMapper;
import com.wallet.mapper.PartnerMapper;
import com.wallet.service.interfaces.IAdminService;
import com.wallet.service.interfaces.IPartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final IAdminService adminService;

    private final IPartnerService partnerService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginFormDTO loginFormDTO){
        String userName = loginFormDTO.getUserName();
        String pass = loginFormDTO.getPassword();

        if (userName == null || userName.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing email");
        }

        if (pass == null || pass.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing password");
        }
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginFormDTO.getUserName(), loginFormDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(user);
            if (user.getPartner() != null){
                PartnerDTO partnerDTO = PartnerMapper.INSTANCE.toDTO(user.getPartner());
                return ResponseEntity.ok(new JwtResponseDTO(partnerDTO, null, token));
            }
            else if (user.getAdmin() !=null) {
                AdminDTO adminDTO = AdminMapper.INSTANCE.toDTO(user.getAdmin());
                return ResponseEntity.ok(new JwtResponseDTO(null, adminDTO, token));
            }  else {
                return ResponseEntity.badRequest().body("Invalid user name or password");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid user name or password");
        }
    }

}

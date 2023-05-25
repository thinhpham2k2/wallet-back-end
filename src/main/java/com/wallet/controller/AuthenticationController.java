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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
@Tag(name = "Authentication API")
@RequestMapping("/api/auth")
@SecurityRequirement(name = "Authorization")
@Slf4j
public class AuthenticationController {

    public static final String ADMIN = "ROLE_Admin";

    public static final String PARTNER = "ROLE_Partner";

    private final IAdminService adminService;

    private final IPartnerService partnerService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signin")
    @Operation(summary = "Login to system")
    public ResponseEntity<?> loginAccount(@RequestBody LoginFormDTO loginFormDTO){
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
            String token = jwtTokenProvider.generateToken(user, 172800000L);
            if (user.getPartner() != null){
                PartnerDTO partnerDTO = PartnerMapper.INSTANCE.toDTO(user.getPartner());
                return ResponseEntity.ok(new JwtResponseDTO(token, partnerDTO, null));
            }
            else if (user.getAdmin() !=null) {
                AdminDTO adminDTO = AdminMapper.INSTANCE.toDTO(user.getAdmin());
                return ResponseEntity.ok(new JwtResponseDTO(token,null, adminDTO));
            }  else {
                return ResponseEntity.badRequest().body("Invalid user name or password");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid user name or password");
        }
    }


}

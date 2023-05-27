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
import com.wallet.service.interfaces.IJwtService;
import com.wallet.service.interfaces.IPartnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    private final IJwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/sign-in")
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
                return ResponseEntity.status(HttpStatus.OK).body(new JwtResponseDTO(token, partnerDTO, null));
            }
            else if (user.getAdmin() !=null) {
                AdminDTO adminDTO = AdminMapper.INSTANCE.toDTO(user.getAdmin());
                return ResponseEntity.status(HttpStatus.OK).body(new JwtResponseDTO(token,null, adminDTO));
            }  else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user name or password");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user name or password");
        }
    }

    @GetMapping("/jwt/refresher")
    @Secured({ADMIN, PARTNER})
    @Operation(summary = "Refresh jwt token")
    public ResponseEntity<?> getUsernameFromJwt(HttpServletRequest request){
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt!=null) {
            String refreshToken = jwtService.refreshJwtToken(jwt, 172800000L);
            if(refreshToken!=null){
                CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (user.getPartner() != null){
                    PartnerDTO partnerDTO = PartnerMapper.INSTANCE.toDTO(user.getPartner());
                    return ResponseEntity.status(HttpStatus.OK).body(new JwtResponseDTO(refreshToken, partnerDTO, null));
                }
                else if (user.getAdmin() !=null) {
                    AdminDTO adminDTO = AdminMapper.INSTANCE.toDTO(user.getAdmin());
                    return ResponseEntity.status(HttpStatus.OK).body(new JwtResponseDTO(refreshToken,null, adminDTO));
                }  else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid jwt token !");
                }
            }
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Invalid jwt token !");
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Not found jwt token !");
    }

    @GetMapping("/google-token/{token}")
    @Operation(summary = "Get JWT token by Google token")
    public ResponseEntity<?> getJwtFromGoogleToken(@PathVariable("token") String googleToken){
        if (googleToken!=null) {
            String jwt = jwtService.getJwtFromGoogleToken(googleToken,172800000L);
            if(jwt!=null){
                return ResponseEntity.status(HttpStatus.OK).body(jwt);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid google token !");
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Not found google token !");
    }

}

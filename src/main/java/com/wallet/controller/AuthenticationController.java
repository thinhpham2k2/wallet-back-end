package com.wallet.controller;

import com.wallet.dto.*;
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
    public ResponseEntity<?> loginAccount(@RequestBody LoginFormDTO loginFormDTO) {
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
            JwtResponseDTO jwtResponseDTO = jwtService.validJwtResponse(token, user);
            if (jwtResponseDTO != null) {
                return ResponseEntity.status(HttpStatus.OK).body(jwtResponseDTO);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user name or password");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user name or password");
        }
    }

    @GetMapping("/jwt/refresher")
    @Secured({ADMIN, PARTNER})
    @Operation(summary = "Refresh jwt token")
    public ResponseEntity<?> refreshTokenByJwt(HttpServletRequest request) {
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt != null) {
            String refreshToken = jwtService.refreshJwtToken(jwt, 172800000L);
            if (refreshToken != null) {
                CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                JwtResponseDTO jwtResponseDTO = jwtService.validJwtResponse(refreshToken, user);
                if (jwtResponseDTO != null) {
                    return ResponseEntity.status(HttpStatus.OK).body(jwtResponseDTO);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid jwt token !");
                }
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid jwt token !");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }

    @PostMapping("/google-token")
    @Operation(summary = "Get JWT token by Google token")
    public ResponseEntity<?> getJwtFromGoogleToken(@RequestParam(value = "token", required = true) String googleToken) {
        if (googleToken != null) {
            JwtResponseDTO jwt = jwtService.getJwtFromGoogleToken(googleToken, 172800000L);
            if (jwt != null) {
                return ResponseEntity.status(HttpStatus.OK).body(jwt);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid google token !");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found google token !");
    }

    @PostMapping("/google-token/register")
    @Operation(summary = "Create account partner for the first time login with Google")
    public ResponseEntity<?> createPartnerByGoogle(@RequestBody PartnerRegisterDTO partnerDTO) {
        if (partnerDTO != null) {
            JwtResponseDTO jwtResponseDTO = partnerService.creatPartner(partnerDTO, 172800000L);
            if (jwtResponseDTO.getPartnerDTO() != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(jwtResponseDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Create partner account failure !");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found partner information !");
        }
    }
}

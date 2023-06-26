package com.wallet.controller;

import com.wallet.dto.JwtResponseDTO;
import com.wallet.dto.LoginFormDTO;
import com.wallet.dto.PartnerRegisterDTO;
import com.wallet.entity.CustomUserDetails;
import com.wallet.jwt.JwtTokenProvider;
import com.wallet.service.interfaces.IJwtService;
import com.wallet.service.interfaces.IPartnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication API")
@RequestMapping("/api/auth")
@SecurityRequirement(name = "Authorization")
public class AuthenticationController {

    public static final String ADMIN = "ROLE_Admin";

    public static final String PARTNER = "ROLE_Partner";

    private final IPartnerService partnerService;

    private final IJwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/sign-in")
    @Operation(summary = "Login to system")
    public ResponseEntity<?> loginAccount(@RequestBody LoginFormDTO loginFormDTO) throws MethodArgumentTypeMismatchException {
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
            String token = jwtTokenProvider.generateToken(user, 17280000000L);
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
    public ResponseEntity<?> refreshTokenByJwt(HttpServletRequest request) throws MethodArgumentTypeMismatchException {
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

    @PostMapping("/google")
    @Operation(summary = "Login with Google")
    public ResponseEntity<?> getJwtFromEmail(@RequestParam(value = "email", required = true) String email) throws MethodArgumentTypeMismatchException {
        if (email != null) {
            JwtResponseDTO jwt = jwtService.getJwtFromEmail(email, 17280000000L);
            if (jwt != null) {
                return ResponseEntity.status(HttpStatus.OK).body(jwt);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid google token !");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found google token !");
    }

    @PostMapping("/google/register")
    @Operation(summary = "Create account partner for the first time login with Google")
    public ResponseEntity<?> createPartnerByGoogle(@RequestBody PartnerRegisterDTO partnerDTO) throws MethodArgumentTypeMismatchException {
        if (partnerDTO != null) {
            JwtResponseDTO jwtResponseDTO = partnerService.creatPartner(partnerDTO, 17280000000L);
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

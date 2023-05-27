package com.wallet.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.wallet.dto.AdminDTO;
import com.wallet.dto.JwtResponseDTO;
import com.wallet.dto.PartnerDTO;
import com.wallet.entity.CustomUserDetails;
import com.wallet.jwt.JwtTokenProvider;
import com.wallet.mapper.AdminMapper;
import com.wallet.mapper.PartnerMapper;
import com.wallet.service.interfaces.IJwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class JwtService implements IJwtService {

    private final CustomUserDetailsService customUserDetailsService;

    public static GoogleIdToken verifyGoogleToken(String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance()).setAudience(Collections.singletonList("783137803189-gqg1hfpemgl7cq9qi8jplrlbaemcanld.apps.googleusercontent.com")).build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            return idToken;
        } else {
            throw new RuntimeException("Invalid ID token.");
        }
    }

    @Override
    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    public String refreshJwtToken(String token, Long jwtExpiration) {
        String refreshToken;
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            String userName = jwtTokenProvider.getUserNameFromJWT(token);
            refreshToken = jwtTokenProvider.generateToken((CustomUserDetails) customUserDetailsService.loadUserByUsername(userName), jwtExpiration);
        } catch (Exception e) {
            return null;
        }
        return refreshToken;
    }

    @Override
    public JwtResponseDTO getJwtFromGoogleToken(String googleToken, Long jwtExpiration) {
        String jwt;
        try {

            GoogleIdToken.Payload payload = verifyGoogleToken(googleToken).getPayload();
            String email = payload.getEmail();

            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByEmail(email);
            jwt = jwtTokenProvider.generateToken(userDetails, jwtExpiration);
            JwtResponseDTO jwtResponseDTO = validJwtResponse(jwt, userDetails);

            return Objects.requireNonNullElseGet(jwtResponseDTO, () -> new JwtResponseDTO(null, new PartnerDTO(null, null, null, null, email, null, null, null, true, true), null));

        } catch (Exception e) {
            throw new InvalidParameterException("Invalid google token !");
        }
    }

    @Override
    public JwtResponseDTO validJwtResponse(String jwt, CustomUserDetails userDetails) {
        if (userDetails.getPartner() != null) {
            PartnerDTO partnerDTO = PartnerMapper.INSTANCE.toDTO(userDetails.getPartner());
            return new JwtResponseDTO(jwt, partnerDTO, null);
        } else if (userDetails.getAdmin() != null) {
            AdminDTO adminDTO = AdminMapper.INSTANCE.toDTO(userDetails.getAdmin());
            return new JwtResponseDTO(jwt, null, adminDTO);
        }
        return null;
    }
}

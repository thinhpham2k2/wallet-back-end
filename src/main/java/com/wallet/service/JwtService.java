package com.wallet.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.wallet.entity.CustomUserDetails;
import com.wallet.jwt.JwtTokenProvider;
import com.wallet.service.interfaces.IJwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
@Transactional
@RequiredArgsConstructor
public class JwtService implements IJwtService {

    private final CustomUserDetailsService customUserDetailsService;

    public static GoogleIdToken verifyGoogleToken(String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList("783137803189-gqg1hfpemgl7cq9qi8jplrlbaemcanld.apps.googleusercontent.com"))
                .build();

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
        }catch (Exception e){
            return null;
        }
        return refreshToken;
    }

    @Override
    public String getJwtFromGoogleToken(String googleToken, Long jwtExpiration) {
        String jwt = null;
        try{

            GoogleIdToken.Payload payload = verifyGoogleToken(googleToken).getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");

            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            jwt = jwtTokenProvider.generateToken((CustomUserDetails) customUserDetailsService.loadUserByEmail(email), jwtExpiration);;
        } catch (Exception e){
            System.out.print("Invalid google token !");
        }

        return jwt;
    }

}

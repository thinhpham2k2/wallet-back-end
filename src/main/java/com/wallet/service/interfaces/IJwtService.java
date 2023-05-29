package com.wallet.service.interfaces;

import com.wallet.dto.JwtResponseDTO;
import com.wallet.entity.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;

public interface IJwtService {

    String getJwtFromRequest(HttpServletRequest request);

    String refreshJwtToken(String token, Long jwtExpiration);

    JwtResponseDTO getJwtFromEmail(String googleToken, Long jwtExpiration);

    JwtResponseDTO validJwtResponse(String jwt, CustomUserDetails userDetails);
}

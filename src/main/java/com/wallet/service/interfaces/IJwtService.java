package com.wallet.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;

public interface IJwtService {

    String getJwtFromRequest(HttpServletRequest request);

    String refreshJwtToken(String token, Long jwtExpiration);

    String getJwtFromGoogleToken(String googleToken, Long jwtExpiration);
}

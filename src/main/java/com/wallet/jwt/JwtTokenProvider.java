package com.wallet.jwt;

import com.wallet.entity.CustomUserDetails;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
@Slf4j
public class JwtTokenProvider {

    private final String JWT_SECRET = "LondonIsBlue";

    private final Long JWT_EXPIRATION = 8640000L;


    public String generateToken(CustomUserDetails userDetails) {
        Date now = new Date();
        String subject = new String();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);
        //Tạo subject cho JWT
        if(userDetails.getPartner()==null){
            subject = userDetails.getAdmin().getUserName();
        }else{

        }
        // Tạo chuỗi json web token từ id của user.
        return Jwts.builder()
                .setSubject(userDetails.getAdmin().getUserName())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid Token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        } catch (SignatureException ex){
            log.error("Signature invalid");
        }
        return false;
    }
}

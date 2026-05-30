package com.payflowx.payment_core.security.jwt;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long expiration;

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }


//Generate Token
    public String generateToken(UUID userId){

        Date now = new Date();

        Date expiryDate = new Date(
                now.getTime() + expiration
        );

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

//Extract User Id
    public UUID extractUserId(String token){

        String subject = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        return UUID.fromString(subject);
    }

//Validate Token
    public boolean isTokenValid(String token){

        try{
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (Exception e) {
            return false;
        }


    }
}
